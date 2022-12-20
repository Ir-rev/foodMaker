package ir.rev.foodMaker.room

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.rev.foodMaker.models.BaseFood

@Database(entities = [BaseFood.Food::class], version = 1)
internal abstract class FoodDataBase : RoomDatabase() {
    abstract val foodDao: FoodDao
}