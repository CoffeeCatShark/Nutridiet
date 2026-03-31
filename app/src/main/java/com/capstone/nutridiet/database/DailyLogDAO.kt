package com.capstone.nutridiet.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DailyLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodEntry(entry: FoodEntry)

    @Transaction
    @Query("SELECT * FROM daily_log WHERE date = :date AND userId = :userId")
    fun getLogWithFoods(userId: Int, date: String): Flow<DailyLogWithFoods?>

    @Query("SELECT * FROM daily_log WHERE date = :date AND userId = :userId LIMIT 1")
    suspend fun getLogByDate(userId: Int, date: String): DailyLog?

    @Query("DELETE FROM food_entry WHERE entryId = :entryId")
    suspend fun deleteFoodEntry(entryId: Int)
}
