package com.capstone.nutridiet.utils

data class BloodPressureResult(
    val systolic: Int,
    val diastolic: Int,  // ✅ added
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
    fun analyze(systolic: Int, diastolic: Int): BloodPressureResult {
        // ✅ uses BOTH readings — whichever is higher determines the level
        val level = when {
            systolic >= 180 || diastolic >= 120 -> HypertensionLevel.CRISIS
            systolic >= 140 || diastolic >= 90  -> HypertensionLevel.STAGE_2
            systolic >= 130 || diastolic >= 80  -> HypertensionLevel.STAGE_1
            systolic >= 120 && diastolic < 80   -> HypertensionLevel.ELEVATED
            else                                -> HypertensionLevel.NORMAL
        }

        val label = when (level) {
            HypertensionLevel.NORMAL   -> "Normal"
            HypertensionLevel.ELEVATED -> "Elevated"
            HypertensionLevel.STAGE_1  -> "Hypertension Stage 1"
            HypertensionLevel.STAGE_2  -> "Hypertension Stage 2"
            HypertensionLevel.CRISIS   -> "Hypertensive Crisis"
        }

        val description = when (level) {
            HypertensionLevel.NORMAL   -> "Your blood pressure is in a healthy range."
            HypertensionLevel.ELEVATED -> "Your blood pressure is slightly above normal. Lifestyle changes are recommended."
            HypertensionLevel.STAGE_1  -> "You have Stage 1 Hypertension. Diet and lifestyle changes are important."
            HypertensionLevel.STAGE_2  -> "You have Stage 2 Hypertension. Medical treatment is likely needed."
            HypertensionLevel.CRISIS   -> "DANGER: Seek emergency medical care immediately."
        }

        return BloodPressureResult(
            systolic = systolic,
            diastolic = diastolic,
            level = level,
            label = label,
            description = description
        )
    }
}