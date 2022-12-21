package ir.rev.foodMaker.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.rev.foodMaker.models.BaseFood


@Dao
internal interface FoodDao {

    @Query("SELECT * FROM Food")
    fun getFoodList(): List<BaseFood.Food>

    @Insert
    fun insertFood(vararg foodList: BaseFood.Food)

    @Update
    fun updateFood(Food: BaseFood.Food)

    @Delete
    fun deleteFood(Food: BaseFood.Food)

}