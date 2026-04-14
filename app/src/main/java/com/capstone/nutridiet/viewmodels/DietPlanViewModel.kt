package com.capstone.nutridiet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nutridiet.BuildConfig
import com.capstone.nutridiet.database.DietPlan
import com.capstone.nutridiet.database.DietPlanDao
import com.capstone.nutridiet.database.FoodDao
import com.capstone.nutridiet.utils.BloodPressureResult
import com.capstone.nutridiet.utils.BloodPressureUtils
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ServerException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class DietPlanViewModel(
    private val dietPlanDao: DietPlanDao,
    private val foodDao: FoodDao
) : ViewModel() {

    // Gemini model — same setup as InsightViewModel
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash-lite",
        apiKey = BuildConfig.GEMINI_API_KEY,
        systemInstruction = content {
            text(
                """
                You are a clinical dietitian specializing in hypertension management.
                Generate diet plans based on blood pressure readings and available foods.
                Keep responses structured and concise.
                Always recommend low-sodium, heart-healthy foods.
            """.trimIndent()
            )
        }
    )

    // Blood pressure state
    private val _bpReading = MutableStateFlow("")
    val bpReading: StateFlow<String> = _bpReading

    private val _bpResult = MutableStateFlow<BloodPressureResult?>(null)
    val bpResult: StateFlow<BloodPressureResult?> = _bpResult

    // Diet plan generation state
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Latest saved diet plan from DB
    val savedDietPlan = dietPlanDao.getLatestDietPlan()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    fun onBpReadingChange(value: String) {
        _bpReading.value = value
        _bpResult.value = null // reset result on change
    }

    fun analyzeBloodPressure() {
        val reading = _bpReading.value.toIntOrNull()
        if (reading == null || reading <= 0) {
            _errorMessage.value = "Please enter a valid blood pressure reading."
            return
        }
        _bpResult.value = BloodPressureUtils.analyze(reading)
        _errorMessage.value = null
    }

    fun generateDietPlan() {
        val result = _bpResult.value ?: return
        viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null
            try {
                // fetch food names from database
                val foodNames = foodDao.GetAllFoodNames().joinToString(", ")

                val prompt = """
                    Patient Blood Pressure: ${result.systolic} mmHg (Systolic)
                    Hypertension Level: ${result.label}
                    
                    Available foods from our database:
                    $foodNames
                    
                    Based on the patient's hypertension level, generate a structured 
                    one-day diet plan using ONLY foods from the list above.
                    
                    Format the response as:
                   - Breakfast: [foods]
                   - Lunch: [foods]
                   - Dinner: [foods]
                   - Snacks: [foods]
                    
                    Then add a brief 2-sentence explanation of why these foods 
                    are suitable for this hypertension level.
                """.trimIndent()

                val response = generateWithRetry(prompt)

                // save to database
                val plan = DietPlan(
                    bloodPressureReading = result.systolic,
                    hypertensionLevel = result.label,
                    generatedPlan = response,
                    generatedAt = LocalDate.now().toString()
                )
                dietPlanDao.clearDietPlans()   // keep only latest
                dietPlanDao.saveDietPlan(plan)

            } catch (e: Exception) {
                _errorMessage.value = "Failed to generate diet plan. Please try again."
            } finally {
                _isGenerating.value = false
            }
        }
    }

    private suspend fun generateWithRetry(prompt: String, maxRetries: Int = 3): String {
        var attempt = 0
        while (attempt < maxRetries) {
            try {
                val response = generativeModel.generateContent(prompt)
                return response.text ?: "No response received."
            } catch (e: ServerException) {
                attempt++
                if (attempt >= maxRetries) throw e
                delay(2000L * attempt)
            } catch (e: kotlinx.serialization.MissingFieldException) {
                // ✅ catch the serialization bug from older SDK versions
                attempt++
                if (attempt >= maxRetries) {
                    return "Service temporarily unavailable. Please try again."
                }
                delay(2000L * attempt)
            } catch (e: Exception) {
                throw e
            }
        }
        return "Failed after $maxRetries attempts."
    }
}