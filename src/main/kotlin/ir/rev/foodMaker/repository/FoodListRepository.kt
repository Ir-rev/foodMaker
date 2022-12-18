package ir.rev.foodMaker.repository

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import ir.rev.foodMaker.models.BaseFood
import ir.rev.foodMaker.models.FoodDetails
import ir.rev.foodMaker.models.FoodFilter
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Репозиторий для получение данные об еде
 */
interface FoodListRepository {

    /**
     * подписка для ослеживания измениния списка еды
     */
    fun getFoodListObservable(): Observable<Pair<List<BaseFood>, Throwable?>>

    /**
     * Возвращает список еды для главного экрана
     */
    fun subscribeFoodList(position: Int, count: Int, foodFilter: FoodFilter)

    /**
     * Возвращает детальную информацию об еде
     */
    fun getFoodDetails(foodId: UUID): Single<FoodDetails>

    /**
     * Возвращает список дополнительной еды (к примеру для "суши" предлагаем "соевый соус")
     */
    fun getAdditionalFood(group: String): Flow<List<BaseFood>>
}