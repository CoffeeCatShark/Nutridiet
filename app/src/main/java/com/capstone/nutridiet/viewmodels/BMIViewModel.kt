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
import kotlinx.coroutines.launch

class BMIViewModel(private val dao: UserBMIDAO): ViewModel() {
    val repository = BMIRepository(dao)
    val height = mutableStateOf<Int>(555555555)
    val weight = mutableStateOf<Int>(555555555)
    val age = mutableStateOf<Int>(555555555)
    val isMale = mutableStateOf<Boolean>(true)

    val bmiActual = mutableStateOf<Double>(repository.bmiActual.value)

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
        loadBMI()
    }


    fun UpdateBMI(
        height: Int,
        weight: Int,
        age: Int,
        isMale: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateBMIData(height,weight,age,isMale)
            this@BMIViewModel.height.value = height
            this@BMIViewModel.weight.value = weight
            this@BMIViewModel.age.value = age
            this@BMIViewModel.isMale.value = isMale
            repository.height.value = height
            repository.weight.value = weight
            repository.age.value = age
            repository.isMale.value = isMale
            repository.loadBMI()
        }
    }


    fun loadBMI() {
        viewModelScope.launch(Dispatchers.IO) {
            height.value = dao.getBMIHeight() ?: 321
            weight.value = dao.getBMIWeight() ?: 321
            age.value = dao.getBMIAge() ?: 321
            isMale.value = dao.getBMIGender() ?: true

            calculateBMI(weight.value,height.value)             //FOR TESTING
            }
        }



    fun calculateBMI(weightLbs: Int, heightIn: Int) {
        bmiActual.value = (weightLbs * 703) / (heightIn * heightIn).toDouble()      //FOR TESTING
    }

}

