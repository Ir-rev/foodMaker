package ir.rev.foodMaker.models

import java.util.UUID

/**
 * Базовое Описание еды для списка на главном экране
 */
sealed class BaseFood(
    val id: UUID,
    val title: String,
    val subTitle: String,
    val group: String,
    val imageUrl: String,
    val price: Double,
    val isAvailability: Boolean
) {

    /**
     * Описание еды для списка на главном экране
     */
    class Food(
        id: UUID,
        title: String,
        subTitle: String,
        group: String,
        imageUrl: String,
        price: Double,
        isAvailability: Boolean
    ) : BaseFood(
        id, title, subTitle, group, imageUrl, price, isAvailability
    )

    /**
     * доп еда по категориям (к примеру для "суши", "соевый соус")
     */
    class AdditionalFood(
        id: UUID,
        title: String,
        subTitle: String,
        group: String,
        imageUrl: String,
        price: Double,
        isAvailability: Boolean
    ) : BaseFood(
        id, title, subTitle, group, imageUrl, price, isAvailability
    )

}

/**
 * Описание еды для детального вида
 */
data class FoodDetails(
    val id: UUID,
    val title: String,
    val aboutFood: AboutFood,
    val additionalFood: List<BaseFood.AdditionalFood>
)

/**
 * Описание еды для детального вида
 */
data class AboutFood(
    val price: Double,
    val description: String,
)
