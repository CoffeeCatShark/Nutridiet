package com.example.yourapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.nutridiet.database.FoodData
import com.capstone.nutridiet.viewmodels.DailyLogViewModel
import com.capstone.nutridiet.viewmodels.FoodViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodPickerScreen(
    foodVM: FoodViewModel,
    dailyLogVM: DailyLogViewModel,
    onFoodAdded: () -> Unit, // navigates back to DailyDietScreen
    onBack: () -> Unit
) {
    val userId = 1 // replace with your actual userId
    val today = LocalDate.now().toString()

    val allFoods by foodVM.allFoods.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var addedFood by remember { mutableStateOf<String?>(null) }

    // filter food list based on search query
    val filteredFoods = remember(searchQuery, allFoods) {
        if (searchQuery.isBlank()) allFoods
        else allFoods.filter {
            it.foodName.contains(searchQuery, ignoreCase = true)
        }
    }

    // show snackbar when food is added
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(addedFood) {
        addedFood?.let {
            snackbarHostState.showSnackbar("$it added to your log!")
            addedFood = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Pick a Food") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search food...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            // Food List
            if (filteredFoods.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No foods found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredFoods) { food ->
                        FoodPickerCard(
                            food = food,
                            onTap = {
                                dailyLogVM.addFood(
                                    userId = userId,
                                    date = today,
                                    foodId = food.foodID ?: 0,
                                    mealType = "General",
                                    servingSize = 1.0
                                )
                                addedFood = food.foodName
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FoodPickerCard(
    food: FoodData,
    onTap: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = food.foodName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${food.foodCalories.toInt()} kcal  •  " +
                            "Protein: ${food.foodProtein.toInt()}g  •  " +
                            "Sat. Fat: ${food.foodSatFat.toInt()}g",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Tap to add",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}