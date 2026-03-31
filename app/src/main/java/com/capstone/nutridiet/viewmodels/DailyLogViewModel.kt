package com.capstone.nutridiet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nutridiet.database.DailyLogDao
import com.capstone.nutridiet.database.DailyLogWithFoods
import com.capstone.nutridiet.database.FoodDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DailyLogViewModel(
    private val dailyLogDao: DailyLogDao,
    private val foodDao: FoodDao
) : ViewModel() {

    private val _dailyLog = MutableStateFlow<DailyLogWithFoods?>(null)
    val dailyLog: StateFlow<DailyLogWithFoods?> = _dailyLog

    fun loadLog(userId: Int, date: String) {
        viewModelScope.launch {
            dailyLogDao.getLogWithFoods(userId, date)
                .collect { log ->        // ✅ collect the Flow
                    _dailyLog.value = log
                }
        }
    }
}