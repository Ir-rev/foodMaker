package ir.rev.foodMaker.repository

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import ir.rev.foodMaker.models.Food
import ir.rev.foodMaker.models.FoodDetails
import ir.rev.foodMaker.utils.isInternetAvailable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import java.net.ConnectException
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.random.nextLong

/**
 * Реализация интерфейса [FoodListRepository]
 */
class FoodListRepositoryImpl : FoodListRepository {

    private val subject = BehaviorSubject.create<Food>()
    private var subscribeDisposable = Disposable.disposed()

    /**
     * подписка для ослеживания измениния количества непрочитаных документов
     */
    override fun getFoodListObservable(): Observable<Food> = subject

    override fun subscribeFoodList(position: Int, count: Int) {
        subscribeDisposable.dispose()
        subscribeDisposable = Single
            .timer(Random.nextLong(500, 2000), TimeUnit.MILLISECONDS)
            .subscribe { it, error ->

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