package ir.rev.foodMaker.repository

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import ir.rev.foodMaker.FoodPlugin
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
internal class FoodListRepositoryImpl : FoodListRepository {

    private val foodSubject = BehaviorSubject.create<Pair<List<BaseFood.Food>, Throwable?>>()
    private var subscribeDisposable = Disposable.disposed()
    private var foodDao = FoodPlugin.dataBase.foodDao
    private var lastFoodFilter: FoodFilter = FoodFilter.default()

    override suspend fun checkDataBaseInit(): Boolean {
        return try {
            foodDao.getFoodList()
            true
        } catch (e: Throwable) {
            false
        }
    }

    /**
     * подписка для ослеживания измениния количества непрочитаных документов
     */
    override fun getFoodListObservable(): Observable<Pair<List<BaseFood.Food>, Throwable?>> = foodSubject

    override fun subscribeFoodList(foodFilter: FoodFilter) {
        subscribeDisposable.dispose()
        lastFoodFilter = foodFilter
        subscribeDisposable =
            Single.fromCallable {
                checkInternet()
            }
                .delay(Random.nextLong(500, 2000), TimeUnit.MILLISECONDS)
                .map {
                    Pair(
                        foodDao.getFoodList()
                            .filterFoodList(foodFilter)
                            .drop(foodFilter.position)
                            .take(foodFilter.count),
                        null
                    )
                }
                .subscribe { it, error ->
                    foodSubject.onNext(
                        if (error != null) {
                            Pair(emptyList(), error)
                        } else {
                            it
                        }
                    )
                }
    }

    private fun List<BaseFood.Food>.filterFoodList(foodFilter: FoodFilter): List<BaseFood.Food> {
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
                getFoodMock().first { it.id == foodId }
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

    override fun getAdditionalFood(group: String): Flow<List<BaseFood.AdditionalFood>> {
        return flow {
            checkInternet()
            emit(Mock.getAdditionalFoodMock())
        }
    }

    override suspend fun addFood(food: BaseFood.Food) {
        foodDao.insertFood(food)
        subscribeFoodList(lastFoodFilter)
    }

    override suspend fun updateFood(food: BaseFood.Food) {
        foodDao.updateFood(food)
        subscribeFoodList(lastFoodFilter)
    }

    override suspend fun deleteFood(food: BaseFood.Food) {
        foodDao.deleteFood(food)
        subscribeFoodList(lastFoodFilter)
    }

    private fun checkInternet() {
        if (isInternetAvailable().not()) throw ConnectException("нет интернета")
    }
}