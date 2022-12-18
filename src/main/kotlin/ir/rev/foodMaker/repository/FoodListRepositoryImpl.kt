package ir.rev.foodMaker.repository

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import ir.rev.foodMaker.models.AboutFood
import ir.rev.foodMaker.models.BaseFood
import ir.rev.foodMaker.models.FoodDetails
import ir.rev.foodMaker.models.FoodFilter
import ir.rev.foodMaker.utils.Mock
import ir.rev.foodMaker.utils.Mock.getFoodMock
import ir.rev.foodMaker.utils.isInternetAvailable
import java.net.ConnectException
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Реализация интерфейса [FoodListRepository]
 */
class FoodListRepositoryImpl : FoodListRepository {

    private val foodSubject = BehaviorSubject.create<Pair<List<BaseFood>, Throwable?>>()
    private var subscribeDisposable = Disposable.disposed()
    private val foodList = getFoodMock()

    /**
     * подписка для ослеживания измениния количества непрочитаных документов
     */
    override fun getFoodListObservable(): Observable<Pair<List<BaseFood>, Throwable?>> = foodSubject

    override fun subscribeFoodList(position: Int, count: Int, foodFilter: FoodFilter) {
        subscribeDisposable.dispose()
        subscribeDisposable =
            Single.fromCallable {
                checkInternet()
            }.delay(Random.nextLong(500, 2000), TimeUnit.MILLISECONDS)
                .subscribe { it, error ->
                    foodSubject.onNext(
                        if (error != null) {
                            Pair(emptyList(), error)
                        } else {
                            Pair(
                                foodList.filterFoodList(foodFilter).drop(position).take(count),
                                null
                            )
                        }
                    )
                }
    }

    private fun List<BaseFood>.filterFoodList(foodFilter: FoodFilter): List<BaseFood> {
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
        return Single.fromCallable { checkInternet() }
            .delay(Random.nextLong(500, 2000), TimeUnit.MILLISECONDS)
            .map {
                getFoodMock().first() { it.id == foodId }
            }.map {
                FoodDetails(
                    id = foodId,
                    title = it.title,
                    aboutFood = AboutFood(
                        price = it.price,
                        description = Mock.getLongDescription()
                    ),
                    additionalFood = Mock.getAdditionalFoodMock(),
                )
            }

    }

    override fun getAdditionalFood(group: String): Flow<List<BaseFood>> {
        return flow {
            checkInternet()
            emit(Mock.getAdditionalFoodMock())
        }
    }

    private fun checkInternet() {
        if (isInternetAvailable().not()) throw ConnectException("нет интернета")
    }
}