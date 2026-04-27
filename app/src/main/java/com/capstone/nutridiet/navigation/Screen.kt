package com.capstone.nutridiet.navigation

sealed class Screen(val route: String, val title: String) {

    object Onboarding : Screen("onboarding", "OnboardingScreen")
    object Dashboard: Screen("dashboard","DashboardScreen")
    object BMI: Screen("bmi","BMIScreen")
    object Food: Screen("food","FoodScreen")
    object Calorie: Screen("calories","CaloriesScreen")
    object Insights: Screen("insights","InsightsScreen")
    object FoodData: Screen("foodData","FoodDataScreen")
    object FoodPicker : Screen("foodpicker","FoodPickerScreen")
    object DietPlan : Screen("dietplan", "DietPlan")
    object BPHistory : Screen("bphistory", "BPHistory")
}