package com.example.yourapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.nutridiet.database.DailyLogWithFoods
import com.capstone.nutridiet.database.FoodEntryWithDetails
import com.capstone.nutridiet.viewmodels.DailyLogViewModel
import java.time.LocalDate

@Composable
fun DailyDietScreen(
    dailyLogViewModel: DailyLogViewModel,
    onAddFood: () -> Unit
) {
    val today = LocalDate.now().toString()
    val userId = 1

    val todayLog by dailyLogViewModel.dailyLog.collectAsState()
    val allLogs by dailyLogViewModel.allLogs.collectAsState()  // ✅ collect all logs

    LaunchedEffect(Unit) {
        dailyLogViewModel.loadLog(userId, today)
        dailyLogViewModel.loadAllLogs(userId)  // ✅ load all logs
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddFood) {
                Icon(Icons.Default.Add, contentDescription = "Add Food")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Header
            item {
                Text(
                    text = "Daily Diet",
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

            // Today's Summary Card
            item { SummaryCard(todayLog) }

            // Today's Food List
            item {
                Text(
                    text = "Today's Foods",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (todayLog == null || todayLog!!.foods.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No foods logged yet.\nTap + to add food.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(todayLog!!.foods) { entry ->
                    FoodEntryCard(
                        entry = entry,
                        onDelete = {
                            dailyLogViewModel.deleteFood(entry.foodEntry.entryId, userId, today)
                        }
                    )
                }
            }

            // Previous Logs
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Previous Logs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            val previousLogs = allLogs.filter { it.dailyLog.date != today }

            if (previousLogs.isEmpty()) {
                item {
                    Text(
                        text = "No previous logs found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(previousLogs) { log ->
                    PreviousLogCard(log)
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// Previous Log Card
@Composable
fun PreviousLogCard(log: DailyLogWithFoods) {
    val totalCalories = log.foods.sumOf { it.foodData?.foodCalories ?: 0.0 }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = log.dailyLog.date,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${totalCalories.toInt()} kcal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${log.foods.size} food(s) logged",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            // list food names
            log.foods.forEach { entry ->
                Text(
                    text = "• ${entry.foodData?.foodName ?: "Unknown"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Summary Card
@Composable
fun SummaryCard(dailyLog: DailyLogWithFoods?) {
    val foods = dailyLog?.foods ?: emptyList()

    val totalCalories = foods.sumOf { it.foodData?.foodCalories ?: 0.0 }
    val totalProtein  = foods.sumOf { it.foodData?.foodProtein ?: 0.0 }
    val totalSodium   = foods.sumOf { it.foodData?.foodSodium ?: 0.0 }
    val totalSatFat   = foods.sumOf { it.foodData?.foodSatFat ?: 0.0 }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Today's Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MacroItem(label = "Calories", value = "${totalCalories.toInt()} kcal")
                MacroItem(label = "Protein",  value = "${totalProtein.toInt()} g")
                MacroItem(label = "Sodium",   value = "${totalSodium.toInt()} mg")
                MacroItem(label = "Sat. Fat", value = "${totalSatFat.toInt()} g")
            }
        }
    }
}

@Composable
fun MacroItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Food Entry Card
@Composable
fun FoodEntryCard(
    entry: FoodEntryWithDetails,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.foodData?.foodName ?: "Unknown Food",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${entry.foodData?.foodCalories?.toInt() ?: 0} kcal  •  " +
                            "Protein: ${entry.foodData?.foodProtein?.toInt() ?: 0}g",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}