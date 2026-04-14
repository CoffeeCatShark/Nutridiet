package com.capstone.nutridiet.utils

data class BloodPressureResult(
    val systolic: Int,
    val diastolic: Int,
    val level: HypertensionLevel,
    val label: String,
    val description: String
)

enum class HypertensionLevel {
    NORMAL,
    ELEVATED,
    STAGE_1,
    STAGE_2,
    CRISIS
}

object BloodPressureUtils {
    fun analyze(reading: Int): BloodPressureResult {
        // reading is systolic only for single input
        return when {
            reading < 120 -> BloodPressureResult(
                systolic = reading,
                diastolic = 0,
                level = HypertensionLevel.NORMAL,
                label = "Normal",
                description = "Your blood pressure is in a healthy range."
            )
            reading < 130 -> BloodPressureResult(
                systolic = reading,
                diastolic = 0,
                level = HypertensionLevel.ELEVATED,
                label = "Elevated",
                description = "Your blood pressure is slightly above normal. Lifestyle changes are recommended."
            )
            reading < 140 -> BloodPressureResult(
                systolic = reading,
                diastolic = 0,
                level = HypertensionLevel.STAGE_1,
                label = "Hypertension Stage 1",
                description = "You have Stage 1 Hypertension. Diet and lifestyle changes are important."
            )
            reading < 180 -> BloodPressureResult(
                systolic = reading,
                diastolic = 0,
                level = HypertensionLevel.STAGE_2,
                label = "Hypertension Stage 2",
                description = "You have Stage 2 Hypertension. Medical treatment is likely needed."
            )
            else -> BloodPressureResult(
                systolic = reading,
                diastolic = 0,
                level = HypertensionLevel.CRISIS,
                label = "Hypertensive Crisis",
                description = "DANGER: Seek emergency medical care immediately."
            )
        }
    }
}