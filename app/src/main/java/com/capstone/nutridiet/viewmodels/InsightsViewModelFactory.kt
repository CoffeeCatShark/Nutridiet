package com.capstone.nutridiet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.nutridiet.database.BloodPressureDao
import com.capstone.nutridiet.database.FoodDao
import com.capstone.nutridiet.database.UserBMIDAO

class InsightViewModelFactory(
    private val bmiDAO: UserBMIDAO,
    private val foodDao: FoodDao,
    private val bpDao: BloodPressureDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InsightViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InsightViewModel(bmiDAO, foodDao, bpDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}