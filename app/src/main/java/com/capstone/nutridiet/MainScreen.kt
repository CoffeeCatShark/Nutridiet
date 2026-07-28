package com.capstone.nutridiet

import DietPlanViewModelFactory
import com.capstone.nutridiet.viewmodels.InsightViewModelFactory
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.capstone.nutridiet.database.BMIDatabase
import com.capstone.nutridiet.database.FoodDatabase
import com.capstone.nutridiet.navigation.Screen
import com.capstone.nutridiet.viewmodels.BMIViewModel
import com.capstone.nutridiet.viewmodels.BMIViewModelFactory
import com.capstone.nutridiet.viewmodels.CalorieViewModel
import com.capstone.nutridiet.viewmodels.CalorieViewModelFactory
import com.capstone.nutridiet.viewmodels.DailyLogViewModel
import com.capstone.nutridiet.viewmodels.DailyLogViewModelFactory
import com.capstone.nutridiet.viewmodels.DietPlanViewModel
import com.capstone.nutridiet.viewmodels.FoodViewModel
import com.capstone.nutridiet.viewmodels.FoodViewModelFactory
import com.capstone.nutridiet.viewmodels.InsightViewModel
import com.example.yourapp.screens.BMIScreen
import com.example.yourapp.screens.CaloriesScreen
import com.example.yourapp.screens.DailyDietScreen
import com.example.yourapp.screens.DietPlanScreen
import com.example.yourapp.screens.FoodPickerScreen
import com.example.yourapp.screens.InsightsScreen
import com.example.yourapp.screens.OnboardingScreen

@Composable
fun MainScreen(
    chatViewModel: InsightViewModel,
    isFirstLaunch: Boolean,
    onFirstLaunchComplete: () -> Unit
) {
    val context = LocalContext.current
    val bmiDB = BMIDatabase.getDatabase(context)
    val foodDB = FoodDatabase.getDatabase(context)
    val bmiDao = bmiDB.dao
    val foodDao = foodDB.dao
    val dailyLogDao = foodDB.dailyLogDao
    val dietPlanDao = foodDB.dietPlanDao
    val bpLogDao = foodDB.bpLogDao  // ✅ instance from foodDB

    val bmiVM: BMIViewModel = viewModel(factory = BMIViewModelFactory(bmiDao))
    val calorieVM: CalorieViewModel = viewModel(factory = CalorieViewModelFactory(bmiDao))
    val foodVM: FoodViewModel = viewModel(factory = FoodViewModelFactory(foodDao))
    val dailyLogVM: DailyLogViewModel = viewModel(
        factory = DailyLogViewModelFactory(dailyLogDao, foodDao)
    )
    val dietPlanVM: DietPlanViewModel = viewModel(
        factory = DietPlanViewModelFactory(dietPlanDao, foodDao, bmiDao, bpLogDao,context)
    )
    val insightVM: InsightViewModel = viewModel(
        factory = InsightViewModelFactory(bmiDao, foodDao, bpLogDao,context)  // ✅ bpLogDao instance
    )

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentRoute =
                    navController.currentBackStackEntryAsState().value?.destination?.route

                NavigationBarItem(
                    selected = currentRoute == Screen.Dashboard.route,
                    onClick = { navController.navigate(Screen.Dashboard.route) },
                    label = { Text("Dashboard") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") }
                )
                NavigationBarItem(
                    selected = currentRoute == Screen.Food.route,
                    onClick = { navController.navigate(Screen.Food.route) },
                    label = { Text("Food") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Food") }
                )
                NavigationBarItem(
                    selected = currentRoute == Screen.Insights.route,
                    onClick = { navController.navigate(Screen.Insights.route) },
                    label = { Text("AI Insights") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Insights") }
                )
                NavigationBarItem(
                    selected = currentRoute == Screen.DietPlan.route,
                    onClick = { navController.navigate(Screen.DietPlan.route) },
                    label = { Text("Diet Plan") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Diet Plan") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isFirstLaunch) Screen.Onboarding.route
            else Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    bmiVM = bmiVM,
                    onOnboardingComplete = {
                        onFirstLaunchComplete()
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Dashboard.route) {
                Dashboard(
                    foodVM = foodVM,
                    dailyLogVM = dailyLogVM,
                    bmiVM = bmiVM,
                    calorieVM = calorieVM,
                    navController = navController
                )
            }
            composable(Screen.BMI.route) {
                BMIScreen(bmiVM)
            }
            composable(Screen.Food.route) {
                DailyDietScreen(
                    dailyLogVM,
                    onAddFood = { navController.navigate(Screen.FoodPicker.route) }
                )
            }
            composable(Screen.Calorie.route) {
                CaloriesScreen(
                    viewModel = calorieVM,
                    insightVM = insightVM,   // ✅ fixed variable name
                    dietPlanVM = dietPlanVM
                )
            }
            composable(Screen.Insights.route) {
                InsightsScreen(insightVM)    // ✅ use insightVM instead of chatViewModel
            }
            composable(Screen.FoodData.route) {
                FoodDataScreen(foodVM)
            }
            composable(Screen.FoodPicker.route) {
                FoodPickerScreen(
                    foodVM = foodVM,
                    dailyLogVM = dailyLogVM,
                    onFoodAdded = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.DietPlan.route) {
                DietPlanScreen(viewModel = dietPlanVM)
            }
            composable(Screen.BPHistory.route) {
                BPHistoryScreen(viewModel = dietPlanVM)
            }
            composable(Screen.NotificationSettings.route) {
                NotificationSettingsScreen()
            }
        }
    }
}