package com.example.yourapp.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContentProviderCompat.requireContext
import com.capstone.nutridiet.viewmodels.BMIViewModel
import com.capstone.nutridiet.viewmodels.DashboardViewModel


@Composable
fun BMIScreen(viewModel: BMIViewModel) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.test()                            //FOR TESTING
        viewModel.repository.loadBMI()
    }


    var ageField by remember { mutableStateOf("") }
    var weightField by remember { mutableStateOf("") }
    var heightField by remember { mutableStateOf("") }
    var isMale by remember { mutableStateOf(true) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(20.dp))               //FOR TESTING
        Text(viewModel.repository.bmiActual.value.toString())              //FOR TESTING
        Spacer(modifier = Modifier.height(20.dp))              //FOR TESTING
        Text(viewModel.repository.weight.value.toString())                 //FOR TESTING


        Text("Input Your BMI")

        OutlinedTextField(
            value = ageField,
            onValueChange = { ageField = it },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = weightField,
            onValueChange = { weightField = it },
            label = { Text("Weight (lbs)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = heightField,
            onValueChange = { heightField = it },
            label = { Text("Height (inches)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text("Gender:", style = MaterialTheme.typography.bodyMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = isMale,
                    onClick = { isMale = true }
                )
                Text("Male")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = !isMale,
                    onClick = { isMale = false }
                )
                Text("Female")
            }
        }

        Button(onClick = {

            if (ageField.isBlank()) {
                Toast.makeText(context, "Please enter age", Toast.LENGTH_SHORT).show()
                return@Button
            }

            if (weightField.isBlank()) {
                Toast.makeText(context, "Please enter weight", Toast.LENGTH_SHORT).show()
                return@Button
            }

            if (heightField.isBlank()) {
                Toast.makeText(context, "Please enter height", Toast.LENGTH_SHORT).show()
                return@Button
            }

            // Convert and validate numeric fields
            val age = ageField.toIntOrNull()
            val weight = weightField.toIntOrNull()
            val height = heightField.toIntOrNull()

            if (age == null || age <= 0) {
                Toast.makeText(context, "Please enter a valid age", Toast.LENGTH_SHORT).show()
                return@Button
            }

            if (weight == null || weight <= 0) {
                Toast.makeText(context, "Please enter a valid weight", Toast.LENGTH_SHORT).show()
                return@Button
            }

            if (height == null || height <= 0) {
                Toast.makeText(context, "Please enter a valid height", Toast.LENGTH_SHORT).show()
                return@Button
            }

            viewModel.UpdateBMI(height,weight,age,isMale)
            ageField = ""
            weightField = ""
            heightField = ""

            Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()

        })
        { Text("Save")}


    }
}