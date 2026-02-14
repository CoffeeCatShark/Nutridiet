package com.capstone.nutridiet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.nutridiet.database.FoodDao

class FoodViewModelFactory(
    private val dao: FoodDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}