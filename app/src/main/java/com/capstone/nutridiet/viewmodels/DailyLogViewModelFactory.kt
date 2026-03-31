package com.capstone.nutridiet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.nutridiet.database.DailyLogDao
import com.capstone.nutridiet.database.FoodDao
import kotlin.jvm.java

class DailyLogViewModelFactory(
    private val dailyLogDao: DailyLogDao,
    private val foodDao: FoodDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DailyLogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DailyLogViewModel(dailyLogDao, foodDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}