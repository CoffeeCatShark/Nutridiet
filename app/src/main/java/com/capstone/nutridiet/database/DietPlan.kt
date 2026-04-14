package com.capstone.nutridiet.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diet_plan")
data class DietPlan(
    @PrimaryKey(autoGenerate = true) val planId: Int = 0,
    val bloodPressureReading: Int,
    val hypertensionLevel: String,
    val generatedPlan: String,  // Gemini's response stored as text
    val generatedAt: String     // date string
)