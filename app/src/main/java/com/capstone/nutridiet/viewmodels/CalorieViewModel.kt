package com.capstone.nutridiet.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nutridiet.BMICalorieUtils
import com.capstone.nutridiet.database.UserBMIDAO
import com.capstone.nutridiet.viewmodels.BMIRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CalorieViewModel(private val dao: UserBMIDAO): ViewModel() {
    val repository = BMIRepository(dao)
    var test_ = mutableStateOf<String>("TEST")

    val dailyCalorieGoal = repository.dailyCalorieGoal
    val currentActivityLevel = repository.currentActivityLevel
    val currentWeightGoal = repository.currentWeightGoal

    fun test(){
        viewModelScope.launch(Dispatchers.IO) {
            this@CalorieViewModel.test_.value = dao.getBMILevel() ?: "HI"
        }
    }

    fun initialize() {
        viewModelScope.launch {
            repository.initializeBMI()
            repository.loadBMI()
        }
    }

    fun setActivityLevel(level: BMICalorieUtils.ActivityLevel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setActivityLevel(level)
            repository.loadBMI()
        }
    }
}