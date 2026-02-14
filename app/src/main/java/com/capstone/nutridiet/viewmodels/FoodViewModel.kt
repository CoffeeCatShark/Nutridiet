package com.capstone.nutridiet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nutridiet.database.FoodDao
import com.capstone.nutridiet.database.FoodData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class FoodViewModel(val dao: FoodDao): ViewModel() {
val repository = FoodRepository(dao)


     fun test(score: Double, id: Int){
         viewModelScope.launch(Dispatchers.IO) {
             repository.test(score,id)
         }
    }

    fun secondTest(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertFood(FoodData(
                "ROOMTEST",
                99.9,
                99.9,
                99.9,
                99.9,
                99.9,
                null
            ))
        }
    }

    val allFoods = repository.allFoodData
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _selectedFood = MutableStateFlow<FoodData?>(null)
    val selectedFood = _selectedFood.asStateFlow()

    fun loadFoodById(id: Int) {
        viewModelScope.launch {
            _selectedFood.value = repository.getFoodById(id)
        }
    }

    fun Delete(food: FoodData){
        viewModelScope.launch {
            repository.deleteFood(food)
        }
    }

}