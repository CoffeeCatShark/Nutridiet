package com.capstone.nutridiet.viewmodels

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

class BMIRepository(private val dao: UserBMIDAO) {

    val height = mutableStateOf<Int>(555555555)
    val weight = mutableStateOf<Int>(555555555)
    val age = mutableStateOf<Int>(555555555)
    val isMale = mutableStateOf<Boolean>(true)
    val bmiActual = mutableStateOf<Double>(99.0)
    private val _weightGoal = MutableStateFlow(BMICalorieUtils.WeightGoal.MAINTAIN)
    val currentWeightGoal = _weightGoal.asStateFlow()
    private val _activityLevel = MutableStateFlow(BMICalorieUtils.ActivityLevel.MODERATE)
    val currentActivityLevel = _activityLevel.asStateFlow()
    suspend fun initializeBMI() {
        val existing = dao.getBMIHeight()
        if (existing == null) {
            dao.UpsertBMIData(
                UserBMIData(
                    bmiID = 0,
                    height = 0,
                    weight = 0,
                    age = 0,
                    isMale = true,
                    activityLevel = BMICalorieUtils.ActivityLevel.MODERATE.name
                )
            )
        }
        calculateBMI(weight.value,height.value)         //ADD loadBMI() IF IT BREAKS
        loadBMI()
        _weightGoal.value = getRecommendedWeightGoal(bmiActual.value)       //TESTING PURPOSES
    }

    suspend fun loadBMI() {
            height.value = dao.getBMIHeight() ?: 321
            weight.value = dao.getBMIWeight() ?: 321
            age.value = dao.getBMIAge() ?: 321
            isMale.value = dao.getBMIGender() ?: true

            calculateBMI(weight.value,height.value)             //FOR TESTING
             _weightGoal.value = getRecommendedWeightGoal(bmiActual.value)
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
            bmiActual.value = 0.00
        }
    }

    ///////////////////////////////////////////////////////////////////////////////
    suspend fun setActivityLevel(level: BMICalorieUtils.ActivityLevel) {
        _activityLevel.value = level
        updateDailyCalorieGoal(level.name)
    }


    suspend fun updateDailyCalorieGoal(level: String) {
        dao.changeActivityLvl(level)
    }


    suspend fun getDailyCalorieGoal(){
            val h = dao.getBMILevel().toString()
            this@BMIRepository._activityLevel.value =
                BMICalorieUtils.ActivityLevel.fromString(h)
    }




}