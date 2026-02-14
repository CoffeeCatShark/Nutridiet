package com.capstone.nutridiet.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class UserBMIData(
    val height: Int,
    val weight: Int,
    val age: Int,
    val isMale: Boolean,
    val activityLevel: String,
    val calorieGoal: Int,

    @PrimaryKey(autoGenerate = false)
    val bmiID: Int
)
