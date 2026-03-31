package com.capstone.nutridiet

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.capstone.nutridiet.navigation.Screen
import androidx.navigation.compose.composable
import com.capstone.nutridiet.database.BMIDatabase
import com.capstone.nutridiet.database.FoodDatabase
import com.capstone.nutridiet.database.UserBMIDAO
import com.capstone.nutridiet.database.UserBMIData
import com.capstone.nutridiet.viewmodels.BMIViewModel
import com.capstone.nutridiet.viewmodels.BMIViewModelFactory
import com.capstone.nutridiet.viewmodels.CalorieViewModel
import com.capstone.nutridiet.viewmodels.CalorieViewModelFactory
import com.capstone.nutridiet.viewmodels.DailyLogViewModel
import com.capstone.nutridiet.viewmodels.DailyLogViewModelFactory
import com.capstone.nutridiet.viewmodels.FoodViewModel
import com.capstone.nutridiet.viewmodels.FoodViewModelFactory
import com.capstone.nutridiet.viewmodels.InsightViewModel
import com.example.yourapp.screens.BMIScreen
import com.example.yourapp.screens.CaloriesScreen
import com.example.yourapp.screens.InsightsScreen
import com.example.yourapp.screens.DailyDietScreen
import com.example.yourapp.screens.FoodPickerScreen

data class NavigationItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

val navItemList = listOf(
    NavigationItem(Screen.Dashboard, Icons.Default.Home,"Dashboard")
)

@Composable
fun MainScreen(chatViewModel: InsightViewModel) {


    val context = LocalContext.current
    val bmiDB = BMIDatabase.getDatabase(context)
    val foodDB = FoodDatabase.getDatabase(context)
    val bmiDao = bmiDB.dao
    val foodDao = foodDB.dao
    val dailyLogDao = foodDB.dailyLogDao

    val bmiVM: BMIViewModel = viewModel(factory = BMIViewModelFactory(bmiDao))
    val calorieVM: CalorieViewModel = viewModel(factory = CalorieViewModelFactory(bmiDao))
    val foodVM: FoodViewModel = viewModel(factory = FoodViewModelFactory(foodDao))
    val dailyLogVM: DailyLogViewModel = viewModel(factory = DailyLogViewModelFactory(
        dailyLogDao,
        foodDao
    )
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
                    selected = currentRoute == Screen.BMI.route,
                    onClick = { navController.navigate(Screen.BMI.route) },
                    label = { Text("BMI") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "BMI") }
                )
                NavigationBarItem(
                    selected = currentRoute == Screen.Food.route,
                    onClick = { navController.navigate(Screen.Food.route) },
                    label = { Text("Food") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Food") }
                )
                NavigationBarItem(
                    selected = currentRoute == Screen.Calorie.route,
                    onClick = { navController.navigate(Screen.Calorie.route) },
                    label = { Text("Calories") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Calories") }
                )
                NavigationBarItem(
                    selected = currentRoute == Screen.Insights.route,
                    onClick = { navController.navigate(Screen.Insights.route) },
                    label = { Text("AI Insights") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Insights") }
                )
            }
        }
    )
    { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                Dashboard(foodVM, navController)
            }
            composable(Screen.BMI.route) {
                BMIScreen(bmiVM)
            }
            composable(Screen.Food.route) {
                DailyDietScreen(dailyLogVM,
                    onAddFood = { navController.navigate(Screen.FoodPicker.route) })
            }
            composable(Screen.Calorie.route) {
                CaloriesScreen(calorieVM)
            }
            composable(Screen.Insights.route) {
                InsightsScreen(chatViewModel)
            }
            composable(Screen.FoodData.route) {
                FoodDataScreen(foodVM)
            }
            composable(Screen.FoodPicker.route) {
                FoodPickerScreen(
                    foodVM = foodVM,
                    dailyLogVM = dailyLogVM,
                    onFoodAdded = { navController.popBackStack() }, // ✅ go back after adding
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}