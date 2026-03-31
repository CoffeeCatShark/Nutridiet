package com.capstone.nutridiet.database

import androidx.room.Entity
import androidx.room.ForeignKey
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

@Entity(tableName = "daily_log")
data class DailyLog(
    @PrimaryKey(autoGenerate = true) val logId: Int = 0,
    val userId: Int,
    val date: String // e.g. "2024-03-31"
)

@Entity(
    tableName = "food_entry",
    foreignKeys = [
        ForeignKey(
            entity = DailyLog::class,
            parentColumns = ["logId"],
            childColumns = ["logOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)


data class FoodEntry(
    @PrimaryKey(autoGenerate = true) val entryId: Int = 0,
    val logOwnerId: Int,   // references DailyLog
    val foodId: Int,       // references FoodData
    val mealType: String,  // "Breakfast", "Lunch", "Dinner", "Snack"
)