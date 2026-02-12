package com.capstone.nutridiet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.nutridiet.database.UserBMIDAO
import kotlin.jvm.java

class CalorieViewModelFactory(
    private val dao: UserBMIDAO
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalorieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalorieViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}