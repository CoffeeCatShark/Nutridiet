package com.capstone.nutridiet.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {

    @Upsert
    suspend fun UpsertFood(foodData: FoodData)

    @Insert
    suspend fun InsertFood(food: FoodData): Long  // returns the generated ID

    @Query("SELECT * FROM FoodData ORDER BY foodID DESC")
    fun GetAllFoods(): Flow<List<FoodData>>

    @Query("SELECT * FROM FoodData WHERE foodID = :foodId")
    suspend fun GetFoodById(foodId: Int): FoodData?

    @Query("SELECT foodName FROM FoodData ORDER BY foodID DESC")
    suspend fun GetAllFoodNames(): List<String>

    @Delete
    suspend fun DeleteFood(foodData: FoodData)

    @Query("""
        UPDATE FoodData
        SET foodScore = :foodScore
        WHERE foodID = :foodId
    """)
    suspend fun UpdateFoodScoring(foodScore: Double, foodId: Int)

}