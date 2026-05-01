package com.capstone.nutridiet.utils

data class BloodPressureResult(
    val systolic: Int,
    val diastolic: Int,
    val level: HypertensionLevel,
    val label: String,
    val description: String,
    val sodiumLimit: Int,        // ✅ calculated here
    val satFatLimitPercent: Int, // ✅ calculated here
    val isEmergency: Boolean     // ✅ calculated here
)

enum class HypertensionLevel {
    NORMAL, ELEVATED, STAGE_1, STAGE_2, CRISIS
}

object BloodPressureAnalyzer {

    // ─── MAIN ANALYSIS ────────────────────────────────
    fun analyze(systolic: Int, diastolic: Int): BloodPressureResult {
        val level = classifyBloodPressure(systolic, diastolic)
        return BloodPressureResult(
            systolic = systolic,
            diastolic = diastolic,
            level = level,
            label = getLabel(level),
            description = getDescription(level),
            sodiumLimit = getSodiumLimit(level),
            satFatLimitPercent = getSatFatLimit(level),
            isEmergency = level == HypertensionLevel.CRISIS
        )
    }

    // ─── CLASSIFICATION ───────────────────────────────
    fun classifyBloodPressure(systolic: Int, diastolic: Int): HypertensionLevel {
        return when {
            systolic >= 180 || diastolic >= 120 -> HypertensionLevel.CRISIS
            systolic >= 140 || diastolic >= 90  -> HypertensionLevel.STAGE_2
            systolic >= 130 || diastolic >= 80  -> HypertensionLevel.STAGE_1
            systolic >= 120 && diastolic < 80   -> HypertensionLevel.ELEVATED
            else                                -> HypertensionLevel.NORMAL
        }
    }

    // ─── SODIUM LIMITS (based on AHA 2017 + DASH) ────
    fun getSodiumLimit(level: HypertensionLevel): Int {
        return when (level) {
            HypertensionLevel.NORMAL   -> 2300
            HypertensionLevel.ELEVATED -> 2300
            HypertensionLevel.STAGE_1  -> 1500
            HypertensionLevel.STAGE_2  -> 1500
            HypertensionLevel.CRISIS   -> 1000
        }
    }

    // ─── SAT FAT LIMITS (AHA recommends <6% of calories)
    fun getSatFatLimit(level: HypertensionLevel): Int {
        return when (level) {
            HypertensionLevel.NORMAL   -> 6
            HypertensionLevel.ELEVATED -> 6
            HypertensionLevel.STAGE_1  -> 6
            HypertensionLevel.STAGE_2  -> 5
            HypertensionLevel.CRISIS   -> 5
        }
    }

    // ─── LABELS ───────────────────────────────────────
    fun getLabel(level: HypertensionLevel): String {
        return when (level) {
            HypertensionLevel.NORMAL   -> "Normal"
            HypertensionLevel.ELEVATED -> "Elevated"
            HypertensionLevel.STAGE_1  -> "Hypertension Stage 1"
            HypertensionLevel.STAGE_2  -> "Hypertension Stage 2"
            HypertensionLevel.CRISIS   -> "Hypertensive Crisis"
        }
    }

    // ─── DESCRIPTIONS ─────────────────────────────────
    fun getDescription(level: HypertensionLevel): String {
        return when (level) {
            HypertensionLevel.NORMAL ->
                "Your blood pressure is in a healthy range."
            HypertensionLevel.ELEVATED ->
                "Your blood pressure is slightly above normal. " +
                        "Lifestyle changes are recommended."
            HypertensionLevel.STAGE_1 ->
                "You have Stage 1 Hypertension. " +
                        "Diet and lifestyle changes are important."
            HypertensionLevel.STAGE_2 ->
                "You have Stage 2 Hypertension. " +
                        "Medical treatment is likely needed."
            HypertensionLevel.CRISIS ->
                "DANGER: Seek emergency medical care immediately."
        }
    }

    // ─── VALIDATION ───────────────────────────────────
    fun isValidReading(systolic: Int, diastolic: Int): Boolean {
        return systolic in 60..300 && diastolic in 40..200
    }

    fun validateInput(
        systolicStr: String,
        diastolicStr: String
    ): ValidationResult {
        val sys = systolicStr.toIntOrNull()
        val dia = diastolicStr.toIntOrNull()

        return when {
            sys == null || dia == null ->
                ValidationResult.Error("Please enter valid numbers.")
            sys <= 0 || dia <= 0 ->
                ValidationResult.Error("Values must be greater than 0.")
            !isValidReading(sys, dia) ->
                ValidationResult.Error(
                    "Reading out of range. " +
                            "Systolic: 60-300, Diastolic: 40-200."
                )
            sys <= dia ->
                ValidationResult.Error(
                    "Systolic must be greater than diastolic."
                )
            else -> ValidationResult.Valid(sys, dia)
        }
    }
}

sealed class ValidationResult {
    data class Valid(val systolic: Int, val diastolic: Int) : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}