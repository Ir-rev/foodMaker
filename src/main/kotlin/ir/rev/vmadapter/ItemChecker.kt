package ir.rev.vmadapter

/**
 * Дополнительные функции для сранения или слияния вью моделей
 */
sealed class ItemChecker<T> {

    /**@SelfDocumented*/
    open fun areItemsTheSame(left: T, right: T) = left == right

    /**@SelfDocumented*/
    open class ForDiffUtils<T: Any> : ItemChecker<T>() {
        /**@SelfDocumented*/
        open fun areContentsTheSame(left: T, right: T) = left == right
    }

    /**@SelfDocumented*/
    open class ForViewModelMerge<T: Any> : ItemChecker<T>() {
        /**@SelfDocumented*/
        open fun merge(left: T, right: T) = Unit
    }
}