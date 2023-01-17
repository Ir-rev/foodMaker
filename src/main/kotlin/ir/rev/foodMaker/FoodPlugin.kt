package ir.rev.foodMaker

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.room.Room
import ir.rev.foodMaker.repository.FoodListRepository
import ir.rev.foodMaker.repository.FoodListRepositoryImpl
import ir.rev.foodMaker.room.FoodDataBase
import ir.rev.foodMaker.utils.Mock

object FoodPlugin {

    private var foodListRepositoryInstance: FoodListRepository? = null
    internal lateinit var dataBase: FoodDataBase
    internal var applicationContext: Context? = null

    /**
     * Возвращает экземпляр репозитория
     */
    fun getFoodListRepository(): FoodListRepository {
        return synchronized(this) {
            foodListRepositoryInstance ?: FoodListRepositoryImpl().also {
                foodListRepositoryInstance = it
            }
        }
    }

    /**
     * Иницилизация базы данных, ВЫЗЫВАТЬ ПРИ СТАРТЕ АПП!!!
     */
    @WorkerThread
    fun initFoodDataBase(context: Context) {
        applicationContext = context
        dataBase = Room.databaseBuilder(
            context,
            FoodDataBase::class.java, "database-name"
        ).build()
        dataBase.foodDao.getFoodList().let {
            if (it.isEmpty()) {
                dataBase.foodDao.insertFood(*Mock.getFoodMock().toTypedArray())
            }
        }
    }

}