package com.example.yourapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.capstone.nutridiet.viewmodels.BMIViewModel

@Composable
fun OnboardingScreen(
    bmiVM: BMIViewModel,
    onOnboardingComplete: () -> Unit
) {
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var isMale by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Header
        Text(
            text = "Welcome to NutriDiet! ",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Let's set up your profile to personalize your experience.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Height
        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Height (cm)") },
            placeholder = { Text("e.g. 170") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Weight
        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight (kg)") },
            placeholder = { Text("e.g. 60") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Age
        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age") },
            placeholder = { Text("e.g. 25") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Gender
        Text(
            text = "Gender",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterChip(
                selected = isMale,
                onClick = { isMale = true },
                label = { Text("Male") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = !isMale,
                onClick = { isMale = false },
                label = { Text("Female") },
                modifier = Modifier.weight(1f)
            )
        }

        // Error
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Submit Button
        Button(
            onClick = {
                val h = height.toIntOrNull()
                val w = weight.toIntOrNull()
                val a = age.toIntOrNull()

                if (h == null || w == null || a == null || h <= 0 || w <= 0 || a <= 0) {
                    errorMessage = "Please fill in all fields with valid values."
                    return@Button
                }

                bmiVM.UpdateBMI(
                    height = h,
                    weight = w,
                    age = a,
                    isMale = isMale
                )
                onOnboardingComplete()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get Started")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}