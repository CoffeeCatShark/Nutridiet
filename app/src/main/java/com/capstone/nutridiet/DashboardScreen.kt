package com.capstone.nutridiet

import android.R.attr.fontWeight
import android.R.attr.onClick
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.capstone.nutridiet.database.DailyLogWithFoods
import com.capstone.nutridiet.navigation.Screen
import com.capstone.nutridiet.viewmodels.BMIViewModel
import com.capstone.nutridiet.viewmodels.CalorieViewModel
import com.capstone.nutridiet.viewmodels.DailyLogViewModel
import com.capstone.nutridiet.viewmodels.FoodViewModel
import com.example.yourapp.screens.SummaryCard
import java.time.LocalDate

@Composable
fun Dashboard(
    foodVM: FoodViewModel,
    dailyLogVM: DailyLogViewModel,
    bmiVM: BMIViewModel,
    calorieVM: CalorieViewModel,
    navController: NavController
) {

    val userId = 1
    val today = LocalDate.now().toString()

    LaunchedEffect(Unit) {
        dailyLogVM.loadLog(userId, today)
        bmiVM.loadBMI()
    }

    val todayLog by dailyLogVM.dailyLog.collectAsState()
    val bmi by bmiVM.bmiActual.collectAsState()
    val weight by bmiVM.weight.collectAsState()
    val height by bmiVM.height.collectAsState()
    val age by bmiVM.age.collectAsState()
    val allFoods by foodVM.allFoods.collectAsState()
    val dailyCalorieGoal by calorieVM.dailyCalorieGoal.collectAsState()
    val activityLevel by calorieVM.currentActivityLevel.collectAsState()
    val weightGoal by calorieVM.currentWeightGoal.collectAsState()
    val currentCalories = todayLog?.foods?.sumOf { it.foodData?.foodCalories ?: 0.0 }?.toInt() ?: 0
    val remainingCalories = dailyCalorieGoal - currentCalories

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = today,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Today's Summary Card (from DailyLogViewModel)
        item {
            Text(
                text = "Today's Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            SummaryCard(todayLog)
        }

        // BMI Card (from BMIViewModel)
        item {
            Text(
                text = "BMI",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "BMI: ${"%.2f".format(bmi)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Category: ${
                            when {
                                bmi < 18.5 -> "Underweight"
                                bmi < 25.0 -> "Normal"
                                bmi < 30.0 -> "Overweight"
                                else       -> "Obese"
                            }
                        }",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Weight: ${weight} lbs  •  Height: ${height} in  •  Age: ${age}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate(Screen.BMI.route) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Edit BMI Details")
                    }
                }
            }
        }

        // Calories Card (from CalorieViewModel)
        item {
            Text(
                text = "Calories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Total Calories: $currentCalories / $dailyCalorieGoal",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate(Screen.Calorie.route) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Edit Your Calorie Goals")
                    }
                }
            }
        }


        // Daily Diet Card (from DailyLogViewModel)
        item {
            Text(
                text = "Daily Diet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${todayLog?.foods?.size ?: 0} food(s) logged today",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate(Screen.Food.route) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Go to Daily Diet")
                    }
                }
            }
        }

        // Food Data Card (from FoodViewModel)
        item {
            Text(
                text = "Food Database",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${allFoods.size} foods available",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate(Screen.FoodData.route) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add/Edit/Remove Foods")
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}