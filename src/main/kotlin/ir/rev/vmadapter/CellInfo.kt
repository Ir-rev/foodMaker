package ir.rev.vmadapter

/**@SelfDocumented*/
data class CellInfo(
    val layoutId: Int,
    val bindingId: Int,
    val itemChecker: ItemChecker<Any>
)