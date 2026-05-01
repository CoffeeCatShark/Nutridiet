package com.capstone.nutridiet.network

data class FoodItemRequest(
    val id: Int,
    val name: String,
    val calories: Double,
    val sodium: Double,
    val protein: Double,
    val satFat: Double,
    val score: Double
)

data class BPHistoryItemRequest(
    val systolic: Int,
    val diastolic: Int,
    val hypertensionLevel: String,
    val date: String
)

data class UserContextRequest(
    val userId: Int,
    val age: Int,
    val isMale: Boolean,
    val height: Int,
    val weight: Int,
    val calorieGoal: Int,
    val activityLevel: String,
    val hypertensionLevel: String,
    val consumedCalories: Double,
    val consumedSodium: Double,
    val consumedProtein: Double,
    val consumedSatFat: Double,
    val sodiumLimit: Int,
    val todayFoodCount: Int,
    val weeklyAvgCalories: Double,
    val weeklyAvgSodium: Double,
    val bpHistory: List<BPHistoryItemRequest>
)

data class QueryRequest(
    val question: String,
    val userContext: UserContextRequest,
    val foods: List<FoodItemRequest>
)

data class QueryResponse(
    val answer: String,
    val retrievedFoods: List<String>
)

data class DietPlanRequest(
    val systolic: Int,
    val diastolic: Int,
    val hypertensionLevel: String,
    val userContext: UserContextRequest,
    val foods: List<FoodItemRequest>
)

data class DietPlanResponse(
    val plan: String,
    val citations: List<String>,
    val retrievedFoods: List<String>
)

data class HealthCheckResponse(
    val status: String,
    val version: String
)