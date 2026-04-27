package com.capstone.nutridiet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.capstone.nutridiet.database.FNRIFood
import com.capstone.nutridiet.database.FoodData
import com.capstone.nutridiet.viewmodels.FoodViewModel

// ─── MAIN SCREEN ──────────────────────────────────────────
@Composable
fun FoodDataScreen(viewModel: FoodViewModel) {

    val allFoods by viewModel.allFoods.collectAsState()
    var currentScreen by remember { mutableStateOf<FoodScreen>(FoodScreen.List) }

    when (val screen = currentScreen) {
        is FoodScreen.List -> {
            FoodListScreen(
                foods = allFoods,
                viewModel = viewModel,
                onAddFood = { currentScreen = FoodScreen.Add },
                onEditFood = { food -> currentScreen = FoodScreen.Edit(food) }
            )
        }
        is FoodScreen.Add -> {
            FoodDataScreen(
                viewModel = viewModel,
                onBack = { currentScreen = FoodScreen.List }
            )
        }
        is FoodScreen.Edit -> {
            DisplayEditFood(
                food = screen.food,
                viewModel = viewModel,
                onBack = { currentScreen = FoodScreen.List }
            )
        }
    }
}

// ─── SCREEN STATES ────────────────────────────────────────
sealed class FoodScreen {
    object List : FoodScreen()
    object Add : FoodScreen()
    data class Edit(val food: FoodData) : FoodScreen()
}

// ─── FOOD LIST SCREEN ─────────────────────────────────────
@Composable
fun FoodListScreen(
    foods: List<FoodData>,
    viewModel: FoodViewModel,
    onAddFood: () -> Unit,
    onEditFood: (FoodData) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<FoodData?>(null) }

    // filter foods by search
    val filteredFoods = remember(searchQuery, foods) {
        if (searchQuery.isBlank()) foods
        else foods.filter {
            it.foodName.contains(searchQuery, ignoreCase = true)
        }
    }

    // delete confirmation dialog
    showDeleteDialog?.let { food ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Food") },
            text = { Text("Are you sure you want to delete '${food.foodName}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.Delete(food)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Delete") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddFood) {
                Icon(Icons.Default.Edit, contentDescription = "Add Food")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Food Database",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${foods.size} foods in database",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search foods...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredFoods.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No foods found.\nTap + to add a food.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredFoods) { food ->
                        FoodItem(
                            food = food,
                            onEdit = { onEditFood(food) },
                            onDelete = { showDeleteDialog = food }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

// ─── FOOD ITEM CARD ───────────────────────────────────────
@Composable
fun FoodItem(
    food: FoodData,
    onEdit: () -> Unit,
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
                    text = food.foodName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${food.foodCalories.toInt()} kcal  •  " +
                            "Na: ${food.foodSodium.toInt()}mg  •  " +
                            "Pro: ${food.foodProtein.toInt()}g  •  " +
                            "SatFat: ${food.foodSatFat}g",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Score: ${food.foodScore}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            // edit button
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            // delete button
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

// ─── ADD FOOD SCREEN ──────────────────────────────────────
@Composable
fun FoodDataScreen(
    viewModel: FoodViewModel,
    onBack: () -> Unit
) {
    var foodName by remember { mutableStateOf("") }
    var foodCalories by remember { mutableStateOf("") }
    var foodSodium by remember { mutableStateOf("") }
    var foodProtein by remember { mutableStateOf("") }
    var foodSatFat by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }

    val fnriSuggestions by viewModel.fnriSuggestions.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // back button + header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Clear, contentDescription = "Back")
            }
            Text(
                text = "Add Food",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // food name + FNRI suggestions
        OutlinedTextField(
            value = foodName,
            onValueChange = {
                foodName = it
                viewModel.searchFNRI(it)
                showSuggestions = it.length >= 2
            },
            label = { Text("Food Name") },
            placeholder = { Text("e.g. Bangus, Kangkong...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                if (foodName.isNotEmpty()) {
                    IconButton(onClick = {
                        foodName = ""
                        viewModel.clearFNRISuggestions()
                        showSuggestions = false
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            }
        )

        // FNRI suggestions dropdown
        if (showSuggestions && fnriSuggestions.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "📋 FNRI Suggestions — tap to autofill",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    fnriSuggestions.forEach { food ->
                        FNRISuggestionItem(
                            food = food,
                            onTap = {
                                foodName = food.name
                                foodCalories = food.calories.toString()
                                foodSodium = food.sodium.toString()
                                foodProtein = food.protein.toString()
                                foodSatFat = food.satFat.toString()
                                showSuggestions = false
                                viewModel.clearFNRISuggestions()
                            }
                        )
                        if (food != fnriSuggestions.last()) HorizontalDivider()
                    }
                }
            }
        }

        // nutritional fields
        NutrientField(
            value = foodCalories,
            onValueChange = { foodCalories = it },
            label = "Calories (kcal)",
            fnriHint = fnriSuggestions.firstOrNull { it.name == foodName }
                ?.let { "FNRI: ${it.calories} kcal/100g" }
        )
        NutrientField(
            value = foodSodium,
            onValueChange = { foodSodium = it },
            label = "Sodium (mg)",
            fnriHint = fnriSuggestions.firstOrNull { it.name == foodName }
                ?.let { "FNRI: ${it.sodium} mg/100g" }
        )
        NutrientField(
            value = foodProtein,
            onValueChange = { foodProtein = it },
            label = "Protein (g)",
            fnriHint = fnriSuggestions.firstOrNull { it.name == foodName }
                ?.let { "FNRI: ${it.protein} g/100g" }
        )
        NutrientField(
            value = foodSatFat,
            onValueChange = { foodSatFat = it },
            label = "Saturated Fat (g)",
            fnriHint = fnriSuggestions.firstOrNull { it.name == foodName }
                ?.let { "FNRI: ${it.satFat} g/100g" }
        )

        // FNRI note
        if (foodName.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "ℹ️ Nutritional values based on FNRI Philippine " +
                            "Food Composition Tables (per 100g serving).",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // save button
        Button(
            onClick = {
                val calories = foodCalories.toDoubleOrNull() ?: 0.0
                val sodium = foodSodium.toDoubleOrNull() ?: 0.0
                val protein = foodProtein.toDoubleOrNull() ?: 0.0
                val satFat = foodSatFat.toDoubleOrNull() ?: 0.0
                val score = viewModel.CalculateFoodScore(sodium, satFat, protein, calories)
                viewModel.SaveNewFoodData(foodName, sodium, satFat, protein, calories, score)
                onBack()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = foodName.isNotBlank() && foodCalories.isNotBlank() &&
                    foodSodium.isNotBlank() && foodProtein.isNotBlank() &&
                    foodSatFat.isNotBlank()
        ) {
            Text("Save Food")
        }
    }
}

// ─── EDIT FOOD SCREEN ─────────────────────────────────────
@Composable
fun DisplayEditFood(
    food: FoodData,
    viewModel: FoodViewModel,
    onBack: () -> Unit
) {
    var nameField by remember { mutableStateOf(food.foodName) }
    var sodiumField by remember { mutableStateOf(food.foodSodium.toString()) }
    var satFatField by remember { mutableStateOf(food.foodSatFat.toString()) }
    var proteinField by remember { mutableStateOf(food.foodProtein.toString()) }
    var caloriesField by remember { mutableStateOf(food.foodCalories.toString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val fnriSuggestions by viewModel.fnriSuggestions.collectAsState()
    var showSuggestions by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // back button + header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Clear, contentDescription = "Back")
            }
            Text(
                text = "Edit Food",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // food name + FNRI suggestions
        OutlinedTextField(
            value = nameField,
            onValueChange = {
                nameField = it
                viewModel.searchFNRI(it)
                showSuggestions = it.length >= 2
            },
            label = { Text("Food Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // FNRI suggestions
        if (showSuggestions && fnriSuggestions.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "📋 FNRI Suggestions — tap to autofill",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    fnriSuggestions.forEach { fnriFood ->
                        FNRISuggestionItem(
                            food = fnriFood,
                            onTap = {
                                nameField = fnriFood.name
                                caloriesField = fnriFood.calories.toString()
                                sodiumField = fnriFood.sodium.toString()
                                proteinField = fnriFood.protein.toString()
                                satFatField = fnriFood.satFat.toString()
                                showSuggestions = false
                                viewModel.clearFNRISuggestions()
                            }
                        )
                        if (fnriFood != fnriSuggestions.last()) HorizontalDivider()
                    }
                }
            }
        }

        // nutritional fields
        NutrientField(
            value = caloriesField,
            onValueChange = { caloriesField = it },
            label = "Calories (kcal)",
            fnriHint = fnriSuggestions.firstOrNull { it.name == nameField }
                ?.let { "FNRI: ${it.calories} kcal/100g" }
        )
        NutrientField(
            value = sodiumField,
            onValueChange = { sodiumField = it },
            label = "Sodium (mg)",
            fnriHint = fnriSuggestions.firstOrNull { it.name == nameField }
                ?.let { "FNRI: ${it.sodium} mg/100g" }
        )
        NutrientField(
            value = proteinField,
            onValueChange = { proteinField = it },
            label = "Protein (g)",
            fnriHint = fnriSuggestions.firstOrNull { it.name == nameField }
                ?.let { "FNRI: ${it.protein} g/100g" }
        )
        NutrientField(
            value = satFatField,
            onValueChange = { satFatField = it },
            label = "Saturated Fat (g)",
            fnriHint = fnriSuggestions.firstOrNull { it.name == nameField }
                ?.let { "FNRI: ${it.satFat} g/100g" }
        )

        // error message
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // save button
        Button(
            onClick = {
                val sodium = sodiumField.toDoubleOrNull()
                val satFat = satFatField.toDoubleOrNull()
                val protein = proteinField.toDoubleOrNull()
                val calories = caloriesField.toDoubleOrNull()

                if (nameField.isBlank() || sodium == null ||
                    satFat == null || protein == null || calories == null) {
                    errorMessage = "Please fill in all fields with valid values."
                    return@Button
                }

                val score = viewModel.CalculateFoodScore(sodium, satFat, protein, calories)
                viewModel.EditFoodData(nameField, sodium, satFat, protein, calories, score)
                onBack()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = nameField.isNotBlank() && caloriesField.isNotBlank() &&
                    sodiumField.isNotBlank() && proteinField.isNotBlank() &&
                    satFatField.isNotBlank()
        ) {
            Text("Save Changes")
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

// ─── REUSABLE NUTRIENT FIELD ──────────────────────────────
@Composable
fun NutrientField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    fnriHint: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        supportingText = fnriHint?.let {
            { Text(it, color = MaterialTheme.colorScheme.primary) }
        }
    )
}

// ─── FNRI SUGGESTION ITEM ────────────────────────────────
@Composable
fun FNRISuggestionItem(
    food: FNRIFood,
    onTap: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() }
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = food.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${food.calories.toInt()} kcal | " +
                        "Na: ${food.sodium.toInt()}mg | " +
                        "Pro: ${food.protein}g | " +
                        "SatFat: ${food.satFat}g",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = food.category,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}