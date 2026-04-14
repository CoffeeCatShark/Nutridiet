package com.capstone.nutridiet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.nutridiet.database.DietPlanDao
import com.capstone.nutridiet.database.FoodDao
import com.capstone.nutridiet.database.UserBMIDAO

class DietPlanViewModelFactory(
    private val dietPlanDao: DietPlanDao,
    private val foodDao: FoodDao,
    private val userDAO: UserBMIDAO
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DietPlanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DietPlanViewModel(dietPlanDao, foodDao, userDAO) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}