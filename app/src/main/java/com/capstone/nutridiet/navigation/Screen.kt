package com.capstone.nutridiet.navigation

sealed class Screen(val route: String, val title: String) {
    object Dashboard: Screen("dashboard","DashboardScreen")
    object BMI: Screen("bmi","BMIScreen")
    object Food: Screen("food","FoodScreen")
    object Calorie: Screen("calories","CaloriesScreen")
    object Insights: Screen("insights","InsightsScreen")
}