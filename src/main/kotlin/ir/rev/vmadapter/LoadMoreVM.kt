package ir.rev.vmadapter

import androidx.databinding.BaseObservable

/**@SelfDocumented*/
data class LoadMoreVM(
    val indeterminate: Boolean = true,
    val delayedShowing: Boolean = false,
) : BaseObservable()