package com.example.yourapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.capstone.nutridiet.utils.HypertensionLevel
import com.capstone.nutridiet.viewmodels.DietPlanViewModel

@Composable
fun DietPlanScreen(viewModel: DietPlanViewModel) {

    val systolic by viewModel.systolic.collectAsState()
    val diastolic by viewModel.diastolic.collectAsState()
    val bpResult by viewModel.bpResult.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val savedDietPlan by viewModel.savedDietPlan.collectAsState()
    val bpHistory by viewModel.bpHistory.collectAsState()

    if (bpHistory.isNotEmpty()) {
        Text(
            text = "Blood Pressure History",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                bpHistory.forEach { log ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${log.systolic}/${log.diastolic} mmHg",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = log.hypertensionLevel,
                            style = MaterialTheme.typography.bodySmall,
                            color = when (log.hypertensionLevel) {
                                "Normal"               -> MaterialTheme.colorScheme.primary
                                "Elevated"             -> MaterialTheme.colorScheme.secondary
                                "Hypertension Stage 1" -> MaterialTheme.colorScheme.tertiary
                                else                   -> MaterialTheme.colorScheme.error
                            }
                        )
                        Text(
                            text = log.date,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Diet Plan",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Enter your blood pressure for a personalized diet plan.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // ✅ Two input fields side by side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = systolic,
                onValueChange = { viewModel.onSystolicChange(it) },
                label = { Text("Systolic") },
                placeholder = { Text("e.g. 120") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true,
                isError = errorMessage != null
            )
            OutlinedTextField(
                value = diastolic,
                onValueChange = { viewModel.onDiastolicChange(it) },
                label = { Text("Diastolic") },
                placeholder = { Text("e.g. 80") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true,
                isError = errorMessage != null
            )
        }

        // ✅ helper text
        Text(
            text = "Format: Systolic / Diastolic  (e.g. 120 / 80)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Error
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Analyze Button
        Button(
            onClick = { viewModel.analyzeBloodPressure() },
            modifier = Modifier.fillMaxWidth(),
            enabled = systolic.isNotBlank() && diastolic.isNotBlank()
        ) {
            Text("Analyze Blood Pressure")
        }

        // BP Result Card
        bpResult?.let { result ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = when (result.level) {
                        HypertensionLevel.NORMAL   -> MaterialTheme.colorScheme.primaryContainer
                        HypertensionLevel.ELEVATED -> MaterialTheme.colorScheme.secondaryContainer
                        HypertensionLevel.STAGE_1  -> MaterialTheme.colorScheme.tertiaryContainer
                        HypertensionLevel.STAGE_2  -> MaterialTheme.colorScheme.errorContainer
                        HypertensionLevel.CRISIS   -> MaterialTheme.colorScheme.errorContainer
                    }
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = result.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Reading: ${result.systolic} / ${result.diastolic} mmHg",  // ✅ shows both
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = result.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.generateDietPlan() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating...")
                        } else {
                            Text("Generate Diet Plan")
                        }
                    }
                }
            }
        }

        // Saved Diet Plan
        savedDietPlan?.let { plan ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your Diet Plan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = plan.hypertensionLevel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Generated: ${plan.generatedAt}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = plan.generatedPlan,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}