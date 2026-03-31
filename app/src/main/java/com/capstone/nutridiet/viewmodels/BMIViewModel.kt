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
    private val _bmiActual = MutableStateFlow<Double>(0.0)
    val bmiActual: StateFlow<Double> = _bmiActual

    private val _height = MutableStateFlow<Int>(0)
    val height: StateFlow<Int> = _height

    private val _weight = MutableStateFlow<Int>(0)
    val weight: StateFlow<Int> = _weight

    private val _age = MutableStateFlow<Int>(0)
    val age: StateFlow<Int> = _age

    private val _isMale = MutableStateFlow<Boolean>(true)
    val isMale: StateFlow<Boolean> = _isMale

    fun test(){
        viewModelScope.launch(Dispatchers.IO) {
            val h = dao.getBMIHeight()
            Log.d("BMI", "Height from DB: $h")
        }
    }



    fun initialize() {
        viewModelScope.launch {
            repository.initializeBMI()
            repository.loadBMI()
        }
    }


    fun UpdateBMI(
        height: Int,
        weight: Int,
        age: Int,
        isMale: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateBMIData(height,weight,age,isMale)
            this@BMIViewModel._height.value = height
            this@BMIViewModel._weight.value = weight
            this@BMIViewModel._age.value = age
            this@BMIViewModel._isMale.value = isMale
            repository.height.value = height
            repository.weight.value = weight
            repository.age.value = age
            repository.isMale.value = isMale
            ////////////////////////////////////////////////////////////////////////////
            repository.getDailyCalorieGoal()
            repository.updateDailyCalorieGoal(repository.currentActivityLevel.value.name)
            calculateBMI(weight,height)
        }
    }


    fun loadBMI() {
        viewModelScope.launch(Dispatchers.IO) {
            _height.value = dao.getBMIHeight() ?: 321
            _weight.value = dao.getBMIWeight() ?: 321
            _age.value = dao.getBMIAge() ?: 321
            _isMale.value = dao.getBMIGender() ?: true

            calculateBMI(weight.value,height.value)             //FOR TESTING
            UpdateBMI(_height.value,_weight.value,_age.value,_isMale.value)
            }
        }



    fun calculateBMI(weightLbs: Int, heightIn: Int) {
        if (heightIn == 0) {_bmiActual.value = 0.99999
            return
        }
        // Convert to Double BEFORE the division to prevent integer rounding
        _bmiActual.value = (weightLbs.toDouble() * 703.0) / (heightIn.toDouble() * heightIn.toDouble())
    }

}

