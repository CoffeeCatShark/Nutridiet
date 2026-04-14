package com.capstone.nutridiet.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DietPlanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDietPlan(plan: DietPlan)

    @Query("SELECT * FROM diet_plan ORDER BY generatedAt DESC LIMIT 1")
    fun getLatestDietPlan(): Flow<DietPlan?>

    @Query("DELETE FROM diet_plan")
    suspend fun clearDietPlans()
}