package com.capstone.nutridiet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nutridiet.BuildConfig
import com.capstone.nutridiet.database.BloodPressureDao
import com.capstone.nutridiet.database.BloodPressureLog
import com.capstone.nutridiet.database.DietPlan
import com.capstone.nutridiet.database.DietPlanDao
import com.capstone.nutridiet.database.FoodDao
import com.capstone.nutridiet.database.UserBMIDAO
import com.capstone.nutridiet.network.BPHistoryItemRequest
import com.capstone.nutridiet.network.DietPlanRequest
import com.capstone.nutridiet.network.FoodItemRequest
import com.capstone.nutridiet.network.RetrofitClient
import com.capstone.nutridiet.network.UserContextRequest
import com.capstone.nutridiet.utils.BloodPressureAnalyzer
import com.capstone.nutridiet.utils.BloodPressureResult
import com.capstone.nutridiet.utils.DietaryGuidelinesHelper
import com.capstone.nutridiet.utils.HypertensionLevel
import com.capstone.nutridiet.utils.ValidationResult
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ServerException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class DietPlanViewModel(
    private val dietPlanDao: DietPlanDao,
    private val foodDao: FoodDao,
    private val userDAO: UserBMIDAO,
    private val bpLogDao: BloodPressureDao
) : ViewModel() {

    // ─── GEMINI — only used for generation ────────────
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash-lite",
        apiKey = BuildConfig.GEMINI_API_KEY,
        systemInstruction = content {
            text("""
                You are a clinical dietitian specializing in 
                hypertension management using the DASH diet.
                Generate diet plans using ONLY the foods provided.
                Always cite AHA or DASH diet guidelines.
                Always include the medical disclaimer.
                Never recommend foods not in the provided list.
            """.trimIndent())
        }
    )

    // ─── UI STATE ─────────────────────────────────────
    private val _systolic = MutableStateFlow("")
    val systolic: StateFlow<String> = _systolic

    private val _diastolic = MutableStateFlow("")
    val diastolic: StateFlow<String> = _diastolic

    private val _bpResult = MutableStateFlow<BloodPressureResult?>(null)
    val bpResult: StateFlow<BloodPressureResult?> = _bpResult

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _citations = MutableStateFlow<List<String>>(emptyList())
    val citations: StateFlow<List<String>> = _citations
    val savedDietPlan = dietPlanDao.getLatestDietPlan()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val bpHistory = bpLogDao.getRecentLogs(userId = 1, limit = 10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ─── INPUT HANDLERS ───────────────────────────────
    fun onSystolicChange(value: String) {
        _systolic.value = value
        _bpResult.value = null
        _errorMessage.value = null
    }

    fun onDiastolicChange(value: String) {
        _diastolic.value = value
        _bpResult.value = null
        _errorMessage.value = null
    }

    // ─── BLOOD PRESSURE ANALYSIS ──────────────────────
    // ✅ delegates to BloodPressureAnalyzer — no logic here
    fun analyzeBloodPressure() {
        val validation = BloodPressureAnalyzer.validateInput(
            _systolic.value,
            _diastolic.value
        )

        when (validation) {
            is ValidationResult.Error -> {
                _errorMessage.value = validation.message
            }
            is ValidationResult.Valid -> {
                val result = BloodPressureAnalyzer.analyze(
                    validation.systolic,
                    validation.diastolic
                )
                _bpResult.value = result
                _errorMessage.value = null

                // save to history
                viewModelScope.launch {
                    bpLogDao.insertLog(
                        BloodPressureLog(
                            userId = 1,
                            systolic = validation.systolic,
                            diastolic = validation.diastolic,
                            hypertensionLevel = result.label,
                            date = LocalDate.now().toString()
                        )
                    )
                }
            }
        }
    }

    // ─── DIET PLAN GENERATION ─────────────────────────
    // ✅ ViewModel only fetches data and calls AI
    // ✅ all logic delegated to helper classes
    fun generateDietPlan() {
        val result = _bpResult.value ?: run {
            _errorMessage.value = "Please analyze your blood pressure first."
            return
        }

        if (result.isEmergency) {
            _errorMessage.value = "⚠️ EMERGENCY: Please seek immediate medical care."
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null
            try {
                // fetch fresh data from DB
                val allFoods = foodDao.GetAllFoodsForRAG()
                val calorieGoal = userDAO.getBMICalorieGoal() ?: 2000
                val height = userDAO.getBMIHeight() ?: 0
                val weight = userDAO.getBMIWeight() ?: 0
                val age = userDAO.getBMIAge() ?: 0
                val isMale = userDAO.getBMIGender() ?: true
                val activityLevel = userDAO.getBMILevel() ?: "MODERATE"
                val recentBP = bpLogDao.getRecentLogs(1, 5).first()

                // ✅ call Firebase RAG /generateDietPlan endpoint
                val response = RetrofitClient.dietPlanApi.generateDietPlan(
                    DietPlanRequest(
                        systolic = result.systolic,
                        diastolic = result.diastolic,
                        hypertensionLevel = result.label,
                        userContext = UserContextRequest(
                            userId = 1,
                            age = age,
                            isMale = isMale,
                            height = height,
                            weight = weight,
                            calorieGoal = calorieGoal,
                            activityLevel = activityLevel,
                            hypertensionLevel = result.label,
                            consumedCalories = 0.0,
                            consumedSodium = 0.0,
                            consumedProtein = 0.0,
                            consumedSatFat = 0.0,
                            sodiumLimit = result.sodiumLimit,
                            todayFoodCount = 0,
                            weeklyAvgCalories = 0.0,
                            weeklyAvgSodium = 0.0,
                            bpHistory = recentBP.map {
                                BPHistoryItemRequest(
                                    systolic = it.systolic,
                                    diastolic = it.diastolic,
                                    hypertensionLevel = it.hypertensionLevel,
                                    date = it.date
                                )
                            }
                        ),
                        foods = allFoods.map {
                            FoodItemRequest(
                                id = it.foodID ?: 0,
                                name = it.foodName,
                                calories = it.foodCalories,
                                sodium = it.foodSodium,
                                protein = it.foodProtein,
                                satFat = it.foodSatFat,
                                score = it.foodScore
                            )
                        }
                    )
                )

                // save to Room DB
                dietPlanDao.clearDietPlans()
                dietPlanDao.saveDietPlan(
                    DietPlan(
                        bloodPressureReading = result.systolic,
                        hypertensionLevel = result.label,
                        generatedPlan = response.plan,
                        generatedAt = LocalDate.now().toString()
                    )
                )

                // save citations
                _citations.value = response.citations

            } catch (e: Exception) {
                _errorMessage.value = "Failed to generate diet plan. Please try again."
            } finally {
                _isGenerating.value = false
            }
        }
    }

    // ─── PROMPT BUILDER ───────────────────────────────
    // ✅ uses DietaryGuidelinesHelper — no hardcoded constraints
    private fun buildPrompt(
        result: BloodPressureResult,
        foodNames: String,
        calorieGoal: Int,
        height: Int,
        weight: Int,
        age: Int,
        isMale: Boolean,
        activityLevel: String,
        bpHistory: String
    ): String {
        return """
            PATIENT PROFILE:
            - Blood Pressure: ${result.systolic}/${result.diastolic} mmHg
            - Hypertension Level: ${result.label}
            - Sodium Limit: ${result.sodiumLimit} mg/day
            - Age: $age | Gender: ${if (isMale) "Male" else "Female"}
            - Height: $height in | Weight: $weight lbs
            - Activity Level: $activityLevel
            - Daily Calorie Goal: $calorieGoal kcal

            BLOOD PRESSURE HISTORY:
            $bpHistory

            AVAILABLE FOODS FROM DATABASE:
            $foodNames

            DIETARY CONSTRAINTS:
            ${DietaryGuidelinesHelper.getDietaryConstraints(result.level)}

            FOODS TO PRIORITIZE:
            ${DietaryGuidelinesHelper.getFoodPriorities(result.level)}

            FOODS TO AVOID:
            ${DietaryGuidelinesHelper.getFoodAvoidances(result.level)}

            SODIUM LIMIT SOURCE:
            ${DietaryGuidelinesHelper.getSodiumLimitText(result.level)}

            TASK:
            Generate a structured 7-DAY DASH diet plan using ONLY
            the foods listed above appropriate for ${result.label}.
            Keep total calories close to $calorieGoal kcal per day.
            Keep total sodium under ${result.sodiumLimit} mg per day.
            Vary meals each day to avoid repetition.

            FORMAT YOUR RESPONSE EXACTLY LIKE THIS:

            ═══════════════════════════════
            📅 DAY 1
            ═══════════════════════════════

            🌅 Breakfast:
            - [food name] — [brief reason]

            🍱 Lunch:
            - [food name] — [brief reason]

            🍽️ Dinner:
            - [food name] — [brief reason]

            🍎 Snacks:
            - [food name] — [brief reason]

            📊 Day 1 Summary:
            - Estimated Calories: [X] / $calorieGoal kcal
            - Estimated Sodium: [X] / ${result.sodiumLimit} mg

            [REPEAT FORMAT FOR DAY 2 THROUGH DAY 7]

            ⚠️ Foods to Avoid:
            - [food name] — [reason based on sodium/sat fat content]

            📋 Clinical Summary:
            [2-3 sentences citing AHA 2017, DASH diet, or WHO guidelines]

            ⚕️ Medical Disclaimer:
            This diet plan is for informational purposes only.
            Always consult a licensed physician or registered
            dietitian before making dietary changes.
            ═══════════════════════════════
        """.trimIndent()
    }

    // ─── GEMINI RETRY ─────────────────────────────────
    private suspend fun generateWithRetry(
        prompt: String,
        maxRetries: Int = 3
    ): String {
        var attempt = 0
        while (attempt < maxRetries) {
            try {
                val response = generativeModel.generateContent(prompt)
                return response.text ?: "No response received."
            } catch (e: ServerException) {
                attempt++
                if (attempt >= maxRetries) throw e
                delay(3000L * attempt)
            } catch (e: kotlinx.serialization.MissingFieldException) {
                attempt++
                if (attempt >= maxRetries) {
                    return "Service temporarily unavailable. Please try again."
                }
                delay(3000L * attempt)
            } catch (e: Exception) {
                throw e
            }
        }
        return "Failed after $maxRetries attempts."
    }
}