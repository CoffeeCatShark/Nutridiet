package com.example.yourapp.screens

import android.R.attr.onClick
import android.R.attr.text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.capstone.nutridiet.BMICalorieUtils
import com.capstone.nutridiet.viewmodels.CalorieViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun CaloriesScreen(viewModel: CalorieViewModel) {
    val context = LocalContext.current
    //val userProfile by viewModel.userProfile.collectAsState()
    val activityLevel by viewModel.repository.currentActivityLevel.collectAsState()
    val weightGoal by viewModel.repository.currentWeightGoal.collectAsState()
    //val dailyGoal by viewModel.dailyCalorieGoal.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.test()                            //FOR TESTING
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item{
            Text("Calorie Settings",
                style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(viewModel.test_.toString())                //FOR TESTING
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "Daily Calorie Goal",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        item {
            // Activity Level Settings
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Activity Level",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BMICalorieUtils.ActivityLevel.entries.forEach{ level ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = activityLevel == level,
                                onClick = {viewModel.setActivityLevel(level) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(level.DisplayName)
                                Text(
                                    text = level.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        item{
            Text("Recommended Weight Goal: ",
                style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = weightGoal.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }




    }






}