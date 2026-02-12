package com.capstone.nutridiet.viewmodels

import android.R.attr.height
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nutridiet.BMICalorieUtils
import com.capstone.nutridiet.BMICalorieUtils.getBMIStatus
import com.capstone.nutridiet.database.UserBMIDAO
import com.capstone.nutridiet.database.UserBMIData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CalorieViewModel(private val dao: UserBMIDAO): ViewModel() {
    val repository = BMIRepository(dao)
    var test_ = mutableStateOf<String>("TEST")
    fun test(){
        viewModelScope.launch(Dispatchers.IO) {
            this@CalorieViewModel.test_.value = dao.getBMILevel()?: "HI"        //FOR TESTING PURPOSES
        }
    }


    fun initialize() {
        viewModelScope.launch { repository.initializeBMI() }
    }


    fun setActivityLevel(level: BMICalorieUtils.ActivityLevel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setActivityLevel(level)
        }
    }


}