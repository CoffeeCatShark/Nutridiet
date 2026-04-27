package com.capstone.nutridiet.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blood_pressure_log")
data class BloodPressureLog(
    @PrimaryKey(autoGenerate = true) val logId: Int = 0,
    val userId: Int = 1,
    val systolic: Int,
    val diastolic: Int,
    val hypertensionLevel: String,
    val date: String  // "2024-03-31"
)