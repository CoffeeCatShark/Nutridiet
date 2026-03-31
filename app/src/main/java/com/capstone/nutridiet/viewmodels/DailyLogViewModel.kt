package com.capstone.nutridiet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nutridiet.database.DailyLog
import com.capstone.nutridiet.database.DailyLogDao
import com.capstone.nutridiet.database.DailyLogWithFoods
import com.capstone.nutridiet.database.FoodDao
import com.capstone.nutridiet.database.FoodEntry
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
                .collect { log ->
                    _dailyLog.value = log
                }
        }
    }

    private val _allLogs = MutableStateFlow<List<DailyLogWithFoods>>(emptyList())
    val allLogs: StateFlow<List<DailyLogWithFoods>> = _allLogs

    fun loadAllLogs(userId: Int) {
        viewModelScope.launch {
            dailyLogDao.getAllLogsWithFoods(userId)
                .collect { logs ->
                    _allLogs.value = logs
                }
        }
    }


    fun addFood(userId: Int, date: String, foodId: Int, mealType: String, servingSize: Double) {
        viewModelScope.launch {
            try {
                val log = getOrCreateLog(userId, date)
                val entry = FoodEntry(
                    logOwnerId = log.logId,
                    foodId = foodId,
                    mealType = mealType,
                )
                dailyLogDao.insertFoodEntry(entry)
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    fun deleteFood(entryId: Int, userId: Int, date: String) {  // ← this is the function
        viewModelScope.launch {
            try {
                dailyLogDao.deleteFoodEntry(entryId)
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    private suspend fun getOrCreateLog(userId: Int, date: String): DailyLog {
        val existing = dailyLogDao.getLogByDate(userId, date)
        return existing ?: run {
            val newLog = DailyLog(userId = userId, date = date)
            val id = dailyLogDao.insertLog(newLog)
            newLog.copy(logId = id.toInt())
        }
    }
}