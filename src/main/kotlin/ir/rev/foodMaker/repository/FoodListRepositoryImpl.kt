package ir.rev.foodMaker.repository

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import ir.rev.foodMaker.models.Food
import ir.rev.foodMaker.models.FoodDetails
import ir.rev.foodMaker.models.FoodFilter
import ir.rev.foodMaker.utils.Mock.getFoodMock
import ir.rev.foodMaker.utils.isInternetAvailable
import kotlinx.coroutines.flow.Flow
import java.net.ConnectException
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Реализация интерфейса [FoodListRepository]
 */
class FoodListRepositoryImpl : FoodListRepository {

    private val foodSubject = BehaviorSubject.create<Pair<List<Food>, Throwable?>>()
    private var subscribeDisposable = Disposable.disposed()

    /**
     * подписка для ослеживания измениния количества непрочитаных документов
     */
    override fun getFoodListObservable(): Observable<Pair<List<Food>, Throwable?>> = foodSubject

    override fun subscribeFoodList(position: Int, count: Int, foodFilter: FoodFilter) {
        subscribeDisposable.dispose()
        subscribeDisposable = Single
            .timer(Random.nextLong(500, 2000), TimeUnit.MILLISECONDS)
            .map { checkInternet() }
            .subscribe { it, error ->
                foodSubject.onNext(
                    if (error != null) {
                        Pair(emptyList(), error)
                    } else {
                        Pair(
                            getFoodMock().filterFoodList(foodFilter).drop(position).take(count),
                            null
                        )
                    }
                )

            }
    }

    private fun List<Food>.filterFoodList(foodFilter: FoodFilter): List<Food> {
        return this.filter { food ->
            foodFilter.title?.let { title ->
                food.title.contains(title)
            } ?: true
        }.filter { food ->
            foodFilter.group?.let { group ->
                food.group == group
            } ?: true
        }.filter { food ->
            foodFilter.isAvailability?.let { isAvailability ->
                food.isAvailability == isAvailability
            } ?: true
        }
    }

    override fun getFoodDetails(foodId: UUID): Single<FoodDetails> {
        TODO()
    }

    override fun getAdditionalFood(group: String): Flow<List<Food>> {
        checkInternet()
        TODO()
    }

    private fun checkInternet() {
        if (isInternetAvailable().not()) throw ConnectException("нет интернета")
    }
}