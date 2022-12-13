package ir.rev.vmadapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

/**
 * Адаптер для viewmodel-ей с использованием DiffUtil
 */
@SuppressLint("all")
open class ViewModelAdapter(
    val mode: Mode = Mode.DIFF_UTILS
): Adapter<ViewHolder>() {

    /**@SelfDocumented*/
    var listUpdateCallback: ListUpdateCallback? = null

    /**@SelfDocumented*/
    protected val items: MutableList<Any> = ArrayList()

    /**@SelfDocumented*/
    val cellMap = LinkedHashMap<Class<out Any>, CellInfo>()

    // Public functions:
    /**
     * Показать элементы
     */
    @Synchronized
    open fun reload(newItems: List<Any>) {
        when (mode) {
            Mode.DIFF_UTILS -> reloadWithDiffUtils(newItems)
            Mode.VIEW_MODEL_MERGE -> reloadWithViewModelMerge(newItems)
        }
    }

    /**@SelfDocumented*/
    @JvmOverloads
    inline fun <reified T : Any> cell(
        @LayoutRes layoutId: Int,
        bindingId: Int = BR.viewModel,
        noinline areItemsTheSame: (T, T) -> Boolean = { a: T, b: T -> a == b },
        noinline areContentsTheSame: (T, T) -> Boolean = { a: T, b: T -> a == b }
    ) {
        if (mode == Mode.VIEW_MODEL_MERGE) throw IllegalStateException("Can't create cell for $mode")

        cell(layoutId, bindingId, object : ItemChecker.ForDiffUtils<T>() {

            override fun areItemsTheSame(left: T, right: T) = areItemsTheSame(left, right)

            override fun areContentsTheSame(left: T, right: T) = areContentsTheSame(left, right)
        })
    }

    @JvmOverloads
    inline fun <reified T : Any> cell(
        @LayoutRes layoutId: Int,
        bindingId: Int = BR.viewModel,
        itemChecker: ItemChecker<T>
    ) {
        when (mode) {
            Mode.DIFF_UTILS ->
                if (itemChecker !is ItemChecker.ForDiffUtils)
                    throw IllegalStateException("Can't create cell for $mode")
            Mode.VIEW_MODEL_MERGE ->
                if (itemChecker !is ItemChecker.ForViewModelMerge)
                    throw IllegalStateException("Can't create cell for $mode")
        }
        @Suppress("UNCHECKED_CAST")
        cellMap[T::class.java] = CellInfo(layoutId, 1, itemChecker as ItemChecker<Any>)
    }

    /**@SelfDocumented*/
    @Synchronized
    protected fun getViewModel(position: Int) = items[position]

    /**@SelfDocumented*/
    fun tryGetItemAt(index: Int): Any? = items.getOrNull(index)

    // RecyclerView.Adapter
    override fun getItemCount() = items.size

    @Synchronized
    override fun getItemViewType(position: Int): Int =
        getCellInfo(getViewModel(position)).layoutId

    @Synchronized
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(viewType, parent, false).rootView)

    @Synchronized
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vm = getViewModel(position)
        holder.onBind(getCellInfo(vm).bindingId, vm)
        holder.binding.root.accessibilityDelegate = ItemSelectionStateChangedNotificator(position, this)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.onRecycled()
    }

    /**@SelfDocumented*/
    @Suppress("unused")
    fun getViewModelType(itemPosition: Int): Class<out Any> {
        return items[itemPosition]::class.java
    }

    private fun reloadWithDiffUtils(newItems: List<Any>) {
        val diffCallback = DiffCallBack(
            items,
            newItems,
            checkAreItemsTheSame = { oldItem: Any, newItem: Any ->
                getCellInfo(oldItem).itemChecker
                    .areItemsTheSame(oldItem, newItem)
            },
            checkAreContentsTheSame = { oldItem: Any, newItem: Any ->
                (getCellInfo(oldItem).itemChecker as ItemChecker.ForDiffUtils<Any>)
                    .areContentsTheSame(oldItem, newItem)
            }
        )
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(object : androidx.recyclerview.widget.ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {
                notifyItemRangeInserted(position, count)
                listUpdateCallback?.onItemsInsertedOnTop(position, count)
                partiallyInvalidateItemDecorations(position)
            }

            override fun onRemoved(position: Int, count: Int) {
                notifyItemRangeRemoved(position, count)
                partiallyInvalidateItemDecorations(position)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                notifyItemMoved(fromPosition, toPosition)
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
                notifyItemRangeChanged(position, count, payload)
            }
        })
    }

    private fun reloadWithViewModelMerge(newItems: List<Any>) {
        // Шаг 1: Переписываем модель и определяем что нужно уведомлять
        val operations = mutableMapOf<Int, Operation>()
        val itemsSize = items.size
        val newItemsSize = newItems.size
        for (i in 0 until newItemsSize) {
            val oldItem = items.getOrNull(i)
            val newItem = newItems[i]
            if (oldItem == null) {
                items.add(i, newItem)
                // Уведомляем о добавлении нового
                operations[i] = Operation.ADD
            } else {
                val itemChecker = getCellInfo(oldItem).itemChecker as ItemChecker.ForViewModelMerge<Any>
                if (
                    oldItem::class.java == newItem::class.java
                    && itemChecker.areItemsTheSame(oldItem, newItem)
                ) {
                    itemChecker.merge(oldItem, newItem)
                    // не нужно уведомлять, т.к. вью модели построены на датабиндинге
                } else {
                    items[i] = newItem
                    // другой элемент, поэтому уведомляем об изменении
                    operations[i] = Operation.CHANGE
                }
            }
        }
        if (itemsSize > newItemsSize) {
            for (i in itemsSize - 1 downTo newItemsSize) {
                items.removeAt(i)
                // уведомить об удалении
                operations[i] = Operation.DELETE
            }
        }
        // Шаг 2: Уведомляем об изменениях
        for (operation in operations) {
            when (operation.value) {
                Operation.ADD -> {
                    notifyItemInserted(operation.key)
                    listUpdateCallback?.onItemsInsertedOnTop(operation.key, 1)
                }
                Operation.DELETE -> notifyItemRemoved(operation.key)
                Operation.CHANGE -> notifyItemChanged(operation.key)
            }
        }
    }

    private fun getCellInfo(viewModel: Any): CellInfo {
        // Find info with simple class check:
        cellMap.entries
            .find { it.key == viewModel.javaClass }
            ?.apply { return value }

        // Find info with inheritance class check:
        cellMap.entries
            .find { it.key.isInstance(viewModel) }
            ?.apply {
                cellMap[viewModel.javaClass] = value
                return value
            }

        val message = "Cell info for class ${viewModel.javaClass.name} not found."
        Log.d("checkResult", message)
        throw Exception(message)
    }

    /**
     * Принудительно перерисовывает [RecyclerView.ItemDecoration],
     * т. к. самостоятельно иногда не перерисовывается.
     * https://stackoverflow.com/a/47355363
     */
    private fun partiallyInvalidateItemDecorations(position: Int) {
        if (position != 0) {
            notifyItemChanged(position - 1, false)
        }
    }

    /**@SelfDocumented*/
    interface ListUpdateCallback {
        /**@SelfDocumented*/
        fun onItemsInsertedOnTop(insertPosition: Int, itemCount: Int)
    }

    /**
     * Режим работы адаптера
     */
    enum class Mode { DIFF_UTILS, VIEW_MODEL_MERGE }

    /**
     * Операция над элементами
     */
    enum class Operation { ADD, DELETE, CHANGE }
}