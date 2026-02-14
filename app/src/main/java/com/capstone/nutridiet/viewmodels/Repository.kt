package com.capstone.nutridiet.viewmodels

import android.R.attr.category
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.capstone.nutridiet.BMICalorieUtils
import com.capstone.nutridiet.database.UserBMIDAO
import com.capstone.nutridiet.database.UserBMIData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

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

    suspend fun updateProfile(){
        loadBMI()
    }

    suspend fun initializeBMI() {
        val existing = dao.getBMIAge()
        if (existing == null) {
            dao.UpsertBMIData(
                UserBMIData(
                    bmiID = 0,
                    height = 170,
                    weight = 60,
                    age = 25,
                    isMale = true,
                    activityLevel = BMICalorieUtils.ActivityLevel.MODERATE.name,
                    calorieGoal = 2000
                )
            )
        }
        loadBMI()
        calculateBMI(weight.value,height.value)         //ADD loadBMI() IF IT BREAKS
        _weightGoal.value = getRecommendedWeightGoal(bmiActual.value)       //TESTING PURPOSES
    }

    suspend fun loadBMI() {
            height.value = dao.getBMIHeight() ?: 321
            weight.value = dao.getBMIWeight() ?: 321
            age.value = dao.getBMIAge() ?: 321
            isMale.value = dao.getBMIGender() ?: true

            calculateBMI(weight.value,height.value)             //FOR TESTING
             _weightGoal.value = getRecommendedWeightGoal(bmiActual.value)
            dao.setCalorieGoal(calculateDailyCalorieGoal(
                weight.value,
                height.value,
                age.value,
                isMale.value,
                currentActivityLevel.value,
                currentWeightGoal.value))
            _calorieGoal.value = dao.getBMICalorieGoal()!!

            getDailyCalorieGoal()
    }

    fun getRecommendedWeightGoal(bmi: Double): BMICalorieUtils.WeightGoal {
        val category = BMICalorieUtils.getBMIStatus(bmi) // Use BMIUtils
        return when (category) {
            "Underweight" -> BMICalorieUtils.WeightGoal.GAIN
            "Normal" -> BMICalorieUtils.WeightGoal.MAINTAIN
            else -> BMICalorieUtils.WeightGoal.LOSE
        }
    }


    fun getBMIStatus(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 25 -> "Normal"
            bmi < 30 -> "Overweight"
            else -> "Obese"
        }
    }

    fun calculateBMI(weightLbs: Int, heightIn: Int){
        if (weightLbs != 0 && heightIn != 0){
            bmiActual.value = (weightLbs * 703) / (heightIn * heightIn).toDouble()
        }
        else{
            bmiActual.value = 9.9876
        }
    }

    ///////////////////////////////////////////////////////////////////////////////
    suspend fun setActivityLevel(level: BMICalorieUtils.ActivityLevel) {
        _activityLevel.value = level
        updateDailyCalorieGoal(level.name.toString())
    }


    suspend fun updateDailyCalorieGoal(level: String) {
        val newGoal = calculateDailyCalorieGoal(
            weight = dao.getBMIWeight() ?: 999,
            height = dao.getBMIHeight() ?: 999,
            age = dao.getBMIAge() ?: 999,
            isMale = dao.getBMIGender() ?: true,
        )
        _calorieGoal.value = newGoal
        dao.changeActivityLvl(level)
        dao.setCalorieGoal(newGoal)
    }


    suspend fun getDailyCalorieGoal() {
        // 1. Get the value from the DAO (it might be null)
        val h = dao.getBMILevel()

        // 2. Use the Elvis operator (?:) to provide a default string if h is null
        // Make sure "MODERATE" matches a valid name in your ActivityLevel enum
        val activityString = h ?: BMICalorieUtils.ActivityLevel.MODERATE.name

        // 3. Pass the guaranteed non-null string to the fromString method
        this@BMIRepository._activityLevel.value =
            BMICalorieUtils.ActivityLevel.fromString(activityString)
    }

    ///////////////////////////////////////////////////////////////////////////////////

     fun calculateBMR(weight: Int, height: Int, age: Int, isMale: Boolean = true): Double {
        return if (isMale) {
            10 * weight + 6.25 * height - 5 * age + 5
        } else {
            10 * weight + 6.25 * height - 5 * age - 161
        }
    }

    /**
     * Calculate Total Daily Energy Expenditure (TDEE) based on activity level
     * This is the total calories needed to maintain current weight
     */
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
        // Validate inputs
        if (weight <= 0 || height <= 0 || age <= 0) {
            return 2000 // Default fallback
        }

        val bmr = calculateBMR(weight, height, age, isMale)
        val tdee = calculateTDEE(bmr, activityLevel)

        // Ensure minimum calorie intake for health
        val baseCalories = when (weightGoal) {
            BMICalorieUtils.WeightGoal.LOSE -> (tdee - 500).coerceAtLeast(1200.0) // Minimum 1200 calories for weight loss
            BMICalorieUtils.WeightGoal.MAINTAIN -> tdee
            BMICalorieUtils.WeightGoal.GAIN -> tdee + 500
        }

        return baseCalories.toInt()
    }

    /////////////////////////////////////////////////////////////////////////////////////


}