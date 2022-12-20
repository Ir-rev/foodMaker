package ir.rev.foodMaker.models

/**
 * фильтр для поиска еды
 */
data class FoodFilter(
    val position: Int,
    val count: Int,
    val title: String? = null,
    val group: String? = null,
    val isAvailability: Boolean? = null,
) {
    companion object {
        fun default() = FoodFilter(
            position = 0,
            count = 10
        )
    }
}