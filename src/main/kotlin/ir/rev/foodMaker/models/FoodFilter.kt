package ir.rev.foodMaker.models

/**
 * фильтр для поиска еды
 */
data class FoodFilter(
    val title: String? = null,
    val group: String? = null,
    val isAvailability: Boolean? = null,
)