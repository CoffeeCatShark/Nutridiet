package com.capstone.nutridiet.viewmodels

import android.R.attr.height
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nutridiet.database.UserBMIDAO
import com.capstone.nutridiet.database.UserBMIData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BMIViewModel(private val dao: UserBMIDAO): ViewModel() {
    val repository = BMIRepository(dao)

    fun test(){
        viewModelScope.launch(Dispatchers.IO) {
            val h = dao.getBMIHeight()
            Log.d("BMI", "Height from DB: $h")
        }
    }

    val height = mutableStateOf<Int>(170)  // cm
    val weight = mutableStateOf<Int>(60)   // kg
    val age = mutableStateOf<Int>(25)
    val isMale = mutableStateOf<Boolean>(true)
    val bmiActual = mutableStateOf<Double>(repository.bmiActual.value)

    fun initialize() {
        viewModelScope.launch {
            repository.initializeBMI()
            repository.loadBMI()
        }
    }


    fun UpdateBMI(height: Int, weight: Int, age: Int, isMale: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateBMIData(height, weight, age, isMale)
            this@BMIViewModel.height.value = height
            this@BMIViewModel.weight.value = weight
            this@BMIViewModel.age.value = age
            this@BMIViewModel.isMale.value = isMale
            repository.height.value = height
            repository.weight.value = weight
            repository.age.value = age
            repository.isMale.value = isMale
            repository.getDailyCalorieGoal()
            repository.updateDailyCalorieGoal(
                repository.currentActivityLevel.value.name
            )
        }
    }


    fun loadBMI() {
        viewModelScope.launch(Dispatchers.IO) {
            height.value = dao.getBMIHeight() ?: 170  // cm
            weight.value = dao.getBMIWeight() ?: 60   // kg
            age.value = dao.getBMIAge() ?: 25
            isMale.value = dao.getBMIGender() ?: true
            calculateBMI(weight.value, height.value)
        }
    }



    fun calculateBMI(weightKg: Int, heightCm: Int) {
        if (heightCm == 0) {
            bmiActual.value = 0.0
            return
        }
        // ✅ metric formula
        val heightM = heightCm / 100.0
        bmiActual.value = weightKg / (heightM * heightM)
    }


}

