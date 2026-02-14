package com.capstone.nutridiet.database

import android.R.attr.height
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert


@Dao
interface UserBMIDAO {

    @Upsert
    suspend fun UpsertBMIData(bmiData: UserBMIData)

    @Query(
        """
    UPDATE UserBMIData
    SET height = :height, 
        weight = :weight,
        age = :age,
        isMale = :isMale
    WHERE bmiID = 0
"""
    )
    suspend fun updateBMIData(height: Int, weight: Int, age: Int, isMale: Boolean)



    @Query("SELECT COUNT(*) FROM UserBMIData WHERE bmiID = 0")
    suspend fun bmiExists(): Int

    @Query("SELECT height FROM UserBMIData WHERE bmiID = 0 ")
    suspend fun getBMIHeight(): Int?

    @Query("SELECT weight FROM UserBMIData WHERE bmiID = 0 ")
    suspend fun getBMIWeight(): Int?

    @Query("SELECT age FROM UserBMIData WHERE bmiID = 0 ")
    suspend fun getBMIAge(): Int?

    @Query("SELECT isMale FROM UserBMIData WHERE bmiID = 0 ")
    suspend fun getBMIGender(): Boolean

    @Query("SELECT activityLevel FROM UserBMIData WHERE bmiID = 0 ")
    suspend fun getBMILevel(): String

    @Query("SELECT calorieGoal FROM UserBMIData WHERE bmiID = 0 ")
    suspend fun getBMICalorieGoal(): Int?

    @Query(
        """
            UPDATE UserBMIData
            SET activityLevel = :activityLevel
            WHERE bmiID = 0
        """
    )
    suspend fun changeActivityLvl(activityLevel: String)

}