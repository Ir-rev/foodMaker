package ir.rev.foodMaker

import ir.rev.foodMaker.repository.FoodListRepository
import ir.rev.foodMaker.repository.FoodListRepositoryImpl

object Plugin {

    private var foodListRepositoryInstance: FoodListRepository? = null

    /**
     * Возвращает экземпляр репозитория
     */
    fun getFoodListRepository(): FoodListRepository {
        return synchronized(this){
            foodListRepositoryInstance ?: FoodListRepositoryImpl().also {
                foodListRepositoryInstance = it
            }
        }
    }
}