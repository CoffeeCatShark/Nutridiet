package com.example.yourapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.nutridiet.BMICalorieUtils
import com.capstone.nutridiet.viewmodels.CalorieViewModel
import com.capstone.nutridiet.viewmodels.DietPlanViewModel
import com.capstone.nutridiet.viewmodels.InsightViewModel

@Composable
fun CaloriesScreen(viewModel: CalorieViewModel,insightVM: InsightViewModel, dietPlanVM: DietPlanViewModel) {

    val activityLevel by viewModel.repository.currentActivityLevel.collectAsState()
    val weightGoal by viewModel.repository.currentWeightGoal.collectAsState()
    val dailyGoal by viewModel.repository.dailyCalorieGoal.collectAsState()

    // ✅ load data when screen opens
    LaunchedEffect(Unit) {
        viewModel.initialize()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ─── Header ───────────────────────────────────
        item {
            Text(
                text = "Calories",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // ─── Daily Calorie Goal Card ───────────────────
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Daily Calorie Goal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$dailyGoal kcal",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Based on your BMI, age, gender and activity level",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // ─── Recommended Weight Goal Card ─────────────
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Recommended Weight Goal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when (weightGoal) {
                            BMICalorieUtils.WeightGoal.LOSE     -> "⬇️ Lose Weight"
                            BMICalorieUtils.WeightGoal.MAINTAIN -> "➡️ Maintain Weight"
                            BMICalorieUtils.WeightGoal.GAIN     -> "⬆️ Gain Weight"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = when (weightGoal) {
                            BMICalorieUtils.WeightGoal.LOSE     ->
                                "Your BMI suggests losing weight. A 500 kcal daily deficit is recommended."
                            BMICalorieUtils.WeightGoal.MAINTAIN ->
                                "Your BMI is in the healthy range. Maintain your current diet."
                            BMICalorieUtils.WeightGoal.GAIN     ->
                                "Your BMI suggests gaining weight. A 500 kcal daily surplus is recommended."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // ─── Activity Level Card ───────────────────────
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Activity Level",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BMICalorieUtils.ActivityLevel.entries.forEach { level ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = activityLevel == level,
                                onClick = { viewModel.setActivityLevel(level); insightVM.refresh(); dietPlanVM}
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = level.DisplayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (activityLevel == level)
                                        FontWeight.Bold else FontWeight.Normal,
                                    color = if (activityLevel == level)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = level.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (level != BMICalorieUtils.ActivityLevel.entries.last()) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }

        // ─── Calorie Breakdown Card ────────────────────
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "How This Is Calculated",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your calorie goal uses the Mifflin-St Jeor equation " +
                                "to calculate your Basal Metabolic Rate (BMR), " +
                                "then multiplied by your activity level multiplier " +
                                "(${activityLevel.multiplier}x) to get your " +
                                "Total Daily Energy Expenditure (TDEE).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Activity multiplier: ${activityLevel.multiplier}x " +
                                "(${activityLevel.DisplayName})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}