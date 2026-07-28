package com.example.yourapp.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContentProviderCompat.requireContext
import com.capstone.nutridiet.viewmodels.BMIViewModel
import com.capstone.nutridiet.viewmodels.DashboardViewModel


@Composable
fun BMIScreen(viewModel: BMIViewModel) {
    val height by viewModel.height
    val weight by viewModel.weight
    val age by viewModel.age
    val isMale by viewModel.isMale
    val bmi by viewModel.bmiActual

    var heightInput by remember { mutableStateOf(height.toString()) }
    var weightInput by remember { mutableStateOf(weight.toString()) }
    var ageInput by remember { mutableStateOf(age.toString()) }
    var isMaleInput by remember { mutableStateOf(isMale) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.loadBMI() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "BMI Calculator",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // ─── BMI Result Card ──────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    bmi < 18.5 -> MaterialTheme.colorScheme.secondaryContainer
                    bmi < 25.0 -> MaterialTheme.colorScheme.primaryContainer
                    bmi < 30.0 -> MaterialTheme.colorScheme.tertiaryContainer
                    else       -> MaterialTheme.colorScheme.errorContainer
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your BMI",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "%.1f".format(bmi),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = when {
                        bmi < 18.5 -> "Underweight"
                        bmi < 25.0 -> "Normal"
                        bmi < 30.0 -> "Overweight"
                        else       -> "Obese"
                    },
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))

                // ✅ show metric values
                Text(
                    text = "Height: ${height} cm  •  Weight: ${weight} kg",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ─── Input Fields ─────────────────────────────
        OutlinedTextField(
            value = heightInput,
            onValueChange = { heightInput = it },
            label = { Text("Height (cm)") },           // ✅ cm
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { Text("100 - 250 cm") }
        )

        OutlinedTextField(
            value = weightInput,
            onValueChange = { weightInput = it },
            label = { Text("Weight (kg)") },           // ✅ kg
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { Text("20 - 300 kg") }
        )

        OutlinedTextField(
            value = ageInput,
            onValueChange = { ageInput = it },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // gender
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
                selected = isMaleInput,
                onClick = { isMaleInput = true },
                label = { Text("Male") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = !isMaleInput,
                onClick = { isMaleInput = false },
                label = { Text("Female") },
                modifier = Modifier.weight(1f)
            )
        }

        // error
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
                val h = heightInput.toIntOrNull()
                val w = weightInput.toIntOrNull()
                val a = ageInput.toIntOrNull()

                when {
                    h == null || w == null || a == null ->
                        errorMessage = "Please fill in all fields."
                    h !in 100..250 ->
                        errorMessage = "Height must be between 100-250 cm."
                    w !in 20..300 ->
                        errorMessage = "Weight must be between 20-300 kg."
                    a !in 1..120 ->
                        errorMessage = "Please enter a valid age."
                    else -> {
                        errorMessage = null
                        viewModel.UpdateBMI(h, w, a, isMaleInput)
                        viewModel.calculateBMI(w, h)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update BMI")
        }

        // ─── BMI Reference Card ───────────────────────
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "BMI Reference (WHO Standard)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                BMIReferenceRow("Underweight", "< 18.5")
                BMIReferenceRow("Normal",      "18.5 - 24.9")
                BMIReferenceRow("Overweight",  "25.0 - 29.9")
                BMIReferenceRow("Obese",       "≥ 30.0")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Source: WHO Global Database on BMI",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun BMIReferenceRow(category: String, range: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = range,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}