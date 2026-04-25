package com.capstone.nutridiet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nutridiet.BuildConfig
import com.capstone.nutridiet.database.DietPlan
import com.capstone.nutridiet.database.DietPlanDao
import com.capstone.nutridiet.database.FoodDao
import com.capstone.nutridiet.database.UserBMIDAO
import com.capstone.nutridiet.utils.BloodPressureResult
import com.capstone.nutridiet.utils.BloodPressureUtils
import com.capstone.nutridiet.utils.HypertensionLevel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ServerException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class DietPlanViewModel(
    private val dietPlanDao: DietPlanDao,
    private val foodDao: FoodDao,
    private val userDAO: UserBMIDAO
) : ViewModel() {

    // ✅ improved system instruction
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash-lite",
        apiKey = BuildConfig.GEMINI_API_KEY,
        systemInstruction = content {
            text("""
                You are a clinical dietitian specializing in hypertension management
                and personalized diet planning based on the DASH diet.
                
                HYPERTENSION GUIDELINES (AHA 2017):
                - Normal:   Systolic < 120  AND Diastolic < 80
                - Elevated: Systolic 120-129 AND Diastolic < 80
                - Stage 1:  Systolic 130-139 OR  Diastolic 80-89
                - Stage 2:  Systolic ≥ 140  OR  Diastolic ≥ 90
                - Crisis:   Systolic ≥ 180  OR  Diastolic ≥ 120
                
                DASH DIET GUIDELINES:
                - Sodium:        limit to 1500-2300mg/day
                - Saturated Fat: limit to less than 6% of total calories
                - Prioritize whole grains, fruits, vegetables, lean proteins
                - Avoid processed foods, high sodium foods, excessive red meat
                
                RULES:
                - Only generate diet plans using foods explicitly provided
                - Always recommend consulting a doctor for medical decisions
                - Keep responses structured and concise
                - Always cite AHA or DASH diet guidelines in your summary
                - Flag crisis level with emergency medical disclaimer
            """.trimIndent())
        }
    )

    // Blood pressure state
    private val _systolic = MutableStateFlow("")
    val systolic: StateFlow<String> = _systolic

    private val _diastolic = MutableStateFlow("")
    val diastolic: StateFlow<String> = _diastolic

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

    fun onSystolicChange(value: String) {
        _systolic.value = value
        _bpResult.value = null
    }

    fun onDiastolicChange(value: String) {
        _diastolic.value = value
        _bpResult.value = null
    }

    fun analyzeBloodPressure() {
        val sys = _systolic.value.toIntOrNull()
        val dia = _diastolic.value.toIntOrNull()

        if (sys == null || dia == null || sys <= 0 || dia <= 0) {
            _errorMessage.value = "Please enter valid systolic and diastolic readings."
            return
        }
        _bpResult.value = BloodPressureUtils.analyze(sys, dia)
        _errorMessage.value = null
    }

    fun generateDietPlan() {
        val result = _bpResult.value ?: run {
            _errorMessage.value = "Please analyze your blood pressure first."
            return
        }
        viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null
            try {
                // ✅ fetch data from DB
                val foodNames = foodDao.GetAllFoodNames().joinToString(", ")
                val calorieGoal = userDAO.getBMICalorieGoal() ?: 2000

                // ✅ use buildPrompt() with all data
                val prompt = buildPrompt(result, foodNames, calorieGoal)
                val response = generateWithRetry(prompt)

                // save to database
                dietPlanDao.clearDietPlans()
                dietPlanDao.saveDietPlan(
                    DietPlan(
                        bloodPressureReading = result.systolic,
                        hypertensionLevel = result.label,
                        generatedPlan = response,
                        generatedAt = LocalDate.now().toString()
                    )
                )
            } catch (e: Exception) {
                _errorMessage.value = "Failed to generate diet plan. Please try again."
            } finally {
                _isGenerating.value = false
            }
        }
    }

    // ✅ updated buildPrompt with calorie goal
    private fun buildPrompt(
        result: BloodPressureResult,
        foodNames: String,
        calorieGoal: Int
    ): String {
        return """
            PATIENT PROFILE:
            - Blood Pressure: ${result.systolic} / ${result.diastolic} mmHg
            - Hypertension Level: ${result.label}
            - Description: ${result.description}
            - Daily Calorie Goal: $calorieGoal kcal
            
            AVAILABLE FOODS FROM DATABASE:
            $foodNames
            
            DIETARY CONSTRAINTS BASED ON HYPERTENSION LEVEL:
            ${getDietaryConstraints(result.level)}
            
            TASK:
            Generate a structured one-day DASH diet plan using ONLY the foods 
            listed above that are appropriate for the patient's hypertension level.
            Try to keep total calories close to the patient's daily calorie goal.
            
            FORMAT YOUR RESPONSE EXACTLY LIKE THIS:
            
            🌅 Breakfast:
            - [food name] — [brief reason why it's suitable]
            
            🍱 Lunch:
            - [food name] — [brief reason why it's suitable]
            
            🍽️ Dinner:
            - [food name] — [brief reason why it's suitable]
            
            🍎 Snacks:
            - [food name] — [brief reason why it's suitable]
            
            ⚠️ Foods to Avoid from the list:
            - [food name] — [brief reason why to avoid]
            
            📋 Summary:
            [2-3 sentences explaining why this plan suits the patient's 
            hypertension level, citing DASH diet or AHA guidelines]
        """.trimIndent()
    }

    private fun getDietaryConstraints(level: HypertensionLevel): String {
        return when (level) {
            HypertensionLevel.NORMAL -> """
                - Maintain a balanced diet
                - Sodium limit: under 2300mg/day
                - Focus on whole foods and lean proteins
                - Maintain healthy weight
            """.trimIndent()

            HypertensionLevel.ELEVATED -> """
                - Begin DASH diet principles
                - Sodium limit: under 2300mg/day
                - Reduce processed and high-sodium foods
                - Increase fruits, vegetables, and whole grains
                - Reduce saturated fat intake
            """.trimIndent()

            HypertensionLevel.STAGE_1 -> """
                - Strict DASH diet required
                - Sodium limit: under 1500mg/day
                - Strictly avoid high sodium foods
                - Prioritize potassium-rich foods
                - Limit saturated fat to under 6% of calories
                - Avoid red meat and processed foods
            """.trimIndent()

            HypertensionLevel.STAGE_2 -> """
                - Strict DASH diet with medical supervision
                - Sodium limit: under 1500mg/day
                - Eliminate all high sodium foods
                - Maximize potassium, magnesium, calcium intake
                - No saturated fat from the food list
                - Only lean proteins allowed
                - No processed or fried foods
            """.trimIndent()

            HypertensionLevel.CRISIS -> """
                - EMERGENCY: Recommend immediate medical attention
                - Strictest sodium restriction: under 1000mg/day
                - Only very low sodium foods
                - No saturated fat whatsoever
                - Only the healthiest foods from the list
                - Include disclaimer to seek emergency care immediately
            """.trimIndent()
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