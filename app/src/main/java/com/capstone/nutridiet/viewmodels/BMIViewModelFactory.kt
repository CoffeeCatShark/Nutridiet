package com.capstone.nutridiet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.nutridiet.database.UserBMIDAO

class BMIViewModelFactory(
    private val dao: UserBMIDAO
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BMIViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BMIViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}