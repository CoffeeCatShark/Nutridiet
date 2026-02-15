package com.capstone.nutridiet

import android.graphics.Paint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.capstone.nutridiet.database.FoodData
import com.capstone.nutridiet.viewmodels.FoodRepository
import com.capstone.nutridiet.viewmodels.FoodViewModel
import kotlinx.coroutines.launch


@Composable
fun FoodDataScreen(viewModel: FoodViewModel) {
    val foods by viewModel.allFoods.collectAsState(initial = emptyList())
    
    var showAddFood by remember { mutableStateOf(false) }


    if (showAddFood) {
        DisplayAddFood(
            onBack = { showAddFood = false },
            viewModel = viewModel
        )
        return
    }

    

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddFood = true
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Food")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text(
                "Food List",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(foods) { food ->
                    FoodItem(food, viewModel)
                }
            }
        }
    }
}




    @Composable
    fun FoodItem(food: FoodData, viewModel: FoodViewModel) {
        var showEditFood by remember { mutableStateOf(false) }

        if (showEditFood) {
            DisplayEditFood(
                onBack = {showEditFood = false},
                viewModel = viewModel,
                food = food
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = food.foodName)
                Text(text = "Calories: ${food.foodCalories}")
                Text(text = "Protein: ${food.foodProtein} g")
                Text(text = "Score: ${food.foodScore}")
                Text(text = "ID: ${food.foodID}")
                Button( onClick = {

                    showEditFood = true

                }) { Text("Edit")}
                Button( onClick = {

                    viewModel.Delete(food)

                }) { Text("Delete")}
            }
        }



}

@Composable
fun DisplayEditFood(onBack: () -> Unit, viewModel: FoodViewModel,food: FoodData){
    Surface() {
        var nameField by remember { mutableStateOf(food.foodName) }
        var sodiumField by remember { mutableStateOf(food.foodSodium.toString()) }
        var satFatField by remember { mutableStateOf(food.foodSatFat.toString()) }
        var proteinField by remember { mutableStateOf(food.foodProtein.toString()) }
        var caloriesField by remember { mutableStateOf(food.foodCalories.toString()) }
        var scoreField by remember { mutableStateOf(food.foodScore) }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            OutlinedTextField(
                value = nameField,
                onValueChange = { nameField = it },
                label = { Text("Food Name") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = sodiumField,
                onValueChange = { sodiumField = it },
                label = { Text("Sodium") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = satFatField,
                onValueChange = { satFatField = it },
                label = { Text("Saturated Fat") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = proteinField,
                onValueChange = { proteinField = it },
                label = { Text("Protein") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = caloriesField,
                onValueChange = { caloriesField = it },
                label = { Text("Calories") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
            )
            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {

                val name = nameField
                val sodium = sodiumField.toDouble()
                val satFat = satFatField.toDouble()
                val protein = proteinField.toDouble()
                val calories = caloriesField.toDouble()
                val score = scoreField

                viewModel.EditFoodData(name,sodium,satFat,protein,calories,score)

                onBack()
            }) {
                Text("Save")
            }
        }
    }

}

@Composable
fun DisplayAddFood(onBack: () -> Unit, viewModel: FoodViewModel){
    var nameField by remember { mutableStateOf("") }
    var sodiumField by remember { mutableStateOf("") }
    var satFatField by remember { mutableStateOf("") }
    var proteinField by remember { mutableStateOf("") }
    var caloriesField by remember { mutableStateOf("") }

    Surface() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
            ) {
            OutlinedTextField(
                value = nameField,
                onValueChange = { nameField = it },
                label = { Text("Food Name") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = sodiumField,
                onValueChange = { sodiumField = it },
                label = { Text("Sodium") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = satFatField,
                onValueChange = { satFatField = it },
                label = { Text("Saturated Fat") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = proteinField,
                onValueChange = { proteinField = it },
                label = { Text("Protein") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = caloriesField,
                onValueChange = { caloriesField = it },
                label = { Text("Calories") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
            )
            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                if(nameField.isBlank() || sodiumField.isBlank() ||
                    satFatField.isBlank() || proteinField.isBlank() || caloriesField.isBlank()){
                    return@Button
                }


                val name = nameField
                val sodium = sodiumField.toDouble()
                val satFat = satFatField.toDouble()
                val protein = proteinField.toDouble()
                val calories = caloriesField.toDouble()
                val score = viewModel.CalculateFoodScore(sodium,satFat,protein,calories)


                viewModel.SaveNewFoodData(name,sodium,satFat,protein,calories,score)
                onBack()
            }
            ) { Text("Save") }
        }
    }
}