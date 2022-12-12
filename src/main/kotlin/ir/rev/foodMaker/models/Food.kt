package ir.rev.foodMaker

import java.util.UUID

/**
 * Описание еды для списка на главном экране
 */
data class Food(
    val id: UUID,
    val title: String,
    val subTitle: String,
    val group: String,
    val price: Double
)

/**
 * Описание еды для детального вида
 */
data class FoodDetails(
    val id: UUID,
    val title: String,
    val aboutFood: AboutFood,
    val additionalFood: List<Food>
)

/**
 * Описание еды для детального вида
 */
data class AboutFood(
    val price: Double,
    val timeCooking: Int,
    val size: String,
    val description: String,
)
