package com.capstone.nutridiet.viewmodels

import com.capstone.nutridiet.database.FoodDao
import com.capstone.nutridiet.database.FoodData

class FoodRepository(private val dao: FoodDao){

    val allFoodData = dao.GetAllFoods()   // Flow<List<FoodData>>

    suspend fun getFoodById(id: Int): FoodData? {
        return dao.GetFoodById(id)
    }

    suspend fun upsertFood(food: FoodData) {
        dao.UpsertFood(food)
    }

    suspend fun insertFood(food: FoodData){
        dao.InsertFood(food)
    }

    suspend fun deleteFood(food: FoodData) {
        dao.DeleteFood(food)
    }

    suspend fun test(score: Double, id: Int){
        dao.UpdateFoodScoring(score,id)
    }

}