package com.capstone.nutridiet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.capstone.nutridiet.database.FoodData
import com.capstone.nutridiet.viewmodels.FoodRepository
import com.capstone.nutridiet.viewmodels.FoodViewModel
import kotlinx.coroutines.launch

@Composable
fun FoodDataScreen(viewModel: FoodViewModel){

        val foods by viewModel.allFoods.collectAsState()
        Text("Food count: ${foods.size}")

        LazyColumn {
            items(foods) { food ->
                FoodItem(food, viewModel)
            }
        }
        Spacer(modifier = Modifier.height(18.dp))

        Button(onClick = {
            viewModel.test(100.0,6)
            //viewModel.secondTest()
        })
        {
            Text("CLICK TEST")
        }

    }

    @Composable
    fun FoodItem(food: FoodData, viewModel: FoodViewModel) {
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



                }) { Text("Edit")}
                Button( onClick = {

                    viewModel.Delete(food)

                }) { Text("Delete")}
            }
        }
}