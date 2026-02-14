package com.capstone.nutridiet.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FoodData(
    val foodName: String,
    val foodSodium: Double,
    val foodProtein: Double,
    val foodCalories: Double,
    val foodScore: Double,
    val foodSatFat: Double,

    @PrimaryKey(autoGenerate = true)
    val foodID: Int?
)
