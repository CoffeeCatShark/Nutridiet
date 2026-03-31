package com.capstone.nutridiet.database
import com.capstone.nutridiet.database.FoodData


import androidx.room.Embedded
import androidx.room.Relation

// FoodEntry with its full FoodData details
data class FoodEntryWithDetails(
    @Embedded val foodEntry: FoodEntry,
    @Relation(
        parentColumn = "foodId",
        entityColumn = "foodID"
    )
    val foodData: FoodData
)

// DailyLog with all food entries and their details
data class DailyLogWithFoods(
    @Embedded val dailyLog: DailyLog,
    @Relation(
        parentColumn = "logId",
        entityColumn = "logOwnerId",
        entity = FoodEntry::class
    )
    val foods: List<FoodEntryWithDetails>
)