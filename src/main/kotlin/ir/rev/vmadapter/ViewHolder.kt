package ir.rev.vmadapter

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView

/**@SelfDocumented*/
class ViewHolder(view: View) : RecyclerView.ViewHolder(view), LifecycleOwner {
    /**@SelfDocumented*/
    val binding: ViewDataBinding = DataBindingUtil.bind(view)!!
    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle() = lifecycleRegistry

    /**@SelfDocumented*/
    fun onBind(
        bindingId: Int,
        viewModel: Any
    ) {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED

        if (bindingId == 0) return

        binding.setVariable(bindingId, viewModel)
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    /**@SelfDocumented*/
    fun onRecycled() {
        /**
         * Потенциально будет переиспользован, поэтому ставим состояние [Lifecycle.State.CREATED],
         * затем [onBind] переведёт в состояние [Lifecycle.State.STARTED].
         * Возникнет краш если в состоянии [Lifecycle.State.DESTROYED] поставить другое состояние.
         */
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }
}