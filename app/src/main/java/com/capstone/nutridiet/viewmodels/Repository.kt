package com.capstone.nutridiet.viewmodels

import androidx.compose.runtime.mutableStateOf
import com.capstone.nutridiet.BMICalorieUtils
import com.capstone.nutridiet.database.UserBMIDAO
import com.capstone.nutridiet.database.UserBMIData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// ❌ REMOVE THIS LINE — it was causing the error
// private val BMIRepository.activityLevel: Any

class BMIRepository(private val dao: UserBMIDAO) {
    val height = mutableStateOf<Int>(555555555)
    val weight = mutableStateOf<Int>(555555555)
    val age = mutableStateOf<Int>(555555555)
    val isMale = mutableStateOf<Boolean>(true)
    val bmiActual = mutableStateOf<Double>(99999999.0)
    private val _weightGoal = MutableStateFlow(BMICalorieUtils.WeightGoal.MAINTAIN)
    val currentWeightGoal = _weightGoal.asStateFlow()
    private val _activityLevel = MutableStateFlow(BMICalorieUtils.ActivityLevel.MODERATE)
    val currentActivityLevel = _activityLevel.asStateFlow()
    private val _calorieGoal = MutableStateFlow(2000)
    val dailyCalorieGoal = _calorieGoal.asStateFlow()

    suspend fun updateProfile() {
        loadBMI()
    }

    suspend fun initializeBMI() {
        val existing = dao.getBMIAge()
        if (existing == null) {
            dao.UpsertBMIData(
                UserBMIData(
                    bmiID = 0,
                    height = 170,   // ✅ cm default
                    weight = 60,    // ✅ kg default
                    age = 25,
                    isMale = true,
                    activityLevel = BMICalorieUtils.ActivityLevel.MODERATE.name,
                    calorieGoal = 2000
                )
            )
        }
        loadBMI()
        calculateBMI(weight.value, height.value)
        _weightGoal.value = getRecommendedWeightGoal(bmiActual.value)
    }

    suspend fun loadBMI() {
        height.value = dao.getBMIHeight() ?: 321
        weight.value = dao.getBMIWeight() ?: 321
        age.value = dao.getBMIAge() ?: 321
        isMale.value = dao.getBMIGender() ?: true

        calculateBMI(weight.value, height.value)
        _weightGoal.value = getRecommendedWeightGoal(bmiActual.value)
        dao.setCalorieGoal(
            calculateDailyCalorieGoal(
                weight.value,
                height.value,
                age.value,
                isMale.value,
                currentActivityLevel.value,
                currentWeightGoal.value
            )
        )
        _calorieGoal.value = dao.getBMICalorieGoal()!!
        getDailyCalorieGoal()
    }

    fun getRecommendedWeightGoal(bmi: Double): BMICalorieUtils.WeightGoal {
        val category = BMICalorieUtils.getBMIStatus(bmi)
        return when (category) {
            "Underweight" -> BMICalorieUtils.WeightGoal.GAIN
            "Normal"      -> BMICalorieUtils.WeightGoal.MAINTAIN
            else          -> BMICalorieUtils.WeightGoal.LOSE
        }
    }

    fun getBMIStatus(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 25   -> "Normal"
            bmi < 30   -> "Overweight"
            else       -> "Obese"
        }
    }

    fun calculateBMI(weightKg: Int, heightCm: Int) {
        if (weightKg != 0 && heightCm != 0) {
            // ✅ metric formula: weight(kg) / height(m)²
            val heightM = heightCm / 100.0
            bmiActual.value = weightKg / (heightM * heightM)
        } else {
            bmiActual.value = 9.9876
        }
    }


    suspend fun setActivityLevel(level: BMICalorieUtils.ActivityLevel) {
        _activityLevel.value = level
        updateDailyCalorieGoal(level.name)
        loadBMI()  // ✅ reload to sync all values
    }

    suspend fun updateDailyCalorieGoal(level: String) {
        // ✅ convert string to enum — no more Any type error
        val activityLevel = BMICalorieUtils.ActivityLevel.fromString(level)

        val newGoal = calculateDailyCalorieGoal(
            weight = dao.getBMIWeight() ?: 999,
            height = dao.getBMIHeight() ?: 999,
            age = dao.getBMIAge() ?: 999,
            isMale = dao.getBMIGender() ?: true,
            activityLevel = activityLevel,        // ✅ correct type now
            weightGoal = currentWeightGoal.value
        )

        // ✅ save to DB first then update StateFlows
        dao.changeActivityLvl(level)
        dao.setCalorieGoal(newGoal)
        _calorieGoal.value = newGoal
        _activityLevel.value = activityLevel      // ✅ correct type now
    }

    suspend fun getDailyCalorieGoal() {
        val h = dao.getBMILevel()
        val activityString = h ?: BMICalorieUtils.ActivityLevel.MODERATE.name
        _activityLevel.value = BMICalorieUtils.ActivityLevel.fromString(activityString)
    }

    fun calculateBMR(
        weight: Int,  // kg
        height: Int,  // cm
        age: Int,
        isMale: Boolean = true
    ): Double {
        return if (isMale) {
            // ✅ metric formula for male
            (10 * weight) + (6.25 * height) - (5 * age) + 5
        } else {
            // ✅ metric formula for female
            (10 * weight) + (6.25 * height) - (5 * age) - 161
        }
    }

    fun calculateTDEE(bmr: Double, activityLevel: BMICalorieUtils.ActivityLevel): Double {
        return bmr * activityLevel.multiplier
    }

    fun calculateDailyCalorieGoal(
        weight: Int,
        height: Int,
        age: Int,
        isMale: Boolean = true,
        activityLevel: BMICalorieUtils.ActivityLevel = BMICalorieUtils.ActivityLevel.MODERATE,
        weightGoal: BMICalorieUtils.WeightGoal = BMICalorieUtils.WeightGoal.MAINTAIN
    ): Int {
        if (weight <= 0 || height <= 0 || age <= 0) return 2000

        val bmr = calculateBMR(weight, height, age, isMale)
        val tdee = calculateTDEE(bmr, activityLevel)

        val baseCalories = when (weightGoal) {
            BMICalorieUtils.WeightGoal.LOSE     -> (tdee - 500).coerceAtLeast(1200.0)
            BMICalorieUtils.WeightGoal.MAINTAIN -> tdee
            BMICalorieUtils.WeightGoal.GAIN     -> tdee + 500
        }

        return baseCalories.toInt()
    }
}