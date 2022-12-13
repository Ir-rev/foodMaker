package ir.rev.foodMaker.repository

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import ir.rev.foodMaker.models.Food
import ir.rev.foodMaker.models.FoodDetails
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Репозиторий для получение данные об еде
 */
interface FoodListRepository {

    /**
     * подписка для ослеживания измениния списка еды
     */
    fun getFoodListObservable(): Observable<Food>

    /**
     * Возвращает список еды для главного экрана
     */
    fun subscribeFoodList(position: Int, count:Int)

    /**
     * Возвращает детальную информацию об еде
     */
    fun getFoodDetails(foodId: UUID): Single<FoodDetails>

    /**
     * Возвращает список дополнительной еды (к примеру для "суши" предлагаем "соевый соус")
     */
    fun getAdditionalFood(group: String): Flow<List<Food>>
}