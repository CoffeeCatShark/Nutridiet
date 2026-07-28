package com.capstone.nutridiet.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nutridiet.BuildConfig
import com.capstone.nutridiet.database.BloodPressureDao
import com.capstone.nutridiet.database.FoodDao
import com.capstone.nutridiet.database.UserBMIDAO
import com.capstone.nutridiet.network.BPHistoryItemRequest
import com.capstone.nutridiet.network.FoodItemRequest
import com.capstone.nutridiet.network.QueryRequest
import com.capstone.nutridiet.network.RetrofitClient
import com.capstone.nutridiet.network.UserContextRequest
import com.capstone.nutridiet.utils.UsageLimiter
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ServerException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InsightViewModel(
    private val bmiDAO: UserBMIDAO,
    private val foodDao: FoodDao,
    private val bpDao: BloodPressureDao,
    private val context: Context
) : ViewModel() {
    private val _remainingInsightUses = MutableStateFlow(
        UsageLimiter.getRemainingInsightUses(context)
    )
    val remainingInsightUses: StateFlow<Int> = _remainingInsightUses
    private var generativeModel: GenerativeModel? = null
    private var chat: com.google.ai.client.generativeai.Chat? = null

    val messageList by lazy { mutableStateListOf<MessageModel>() }

    // ✅ initialize model after loading data from DB
    fun initialize() {
        viewModelScope.launch(Dispatchers.IO) {
            val foodNames = foodDao.GetAllFoodNames().joinToString(", ")
            val bmiLevel = bmiDAO.getBMILevel() ?: "Unknown"
            val bmiCalGoal = bmiDAO.getBMICalorieGoal()?.toString() ?: "Unknown"

            val bpHistory = bpDao.getRecentLogs(userId = 1, limit = 10)
                .first()  // gets the current list once
                .joinToString("\n") { log ->
                    "- ${log.date}: ${log.systolic}/${log.diastolic} mmHg — ${log.hypertensionLevel}"
                }
                .ifEmpty { "No blood pressure history recorded." }

            withContext(Dispatchers.Main) {
                generativeModel = GenerativeModel(
                    modelName = "gemini-2.5-flash-lite",
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    systemInstruction = content {
                        text("""
    You are a clinical nutrition assistant for NutriDiet,
    a Filipino hypertension diet management application.
    
    YOU CAN ANSWER:
    - Questions about the user's blood pressure readings and history
    - Questions about hypertension levels and what they mean
    - Questions about the DASH diet and sodium limits
    - Questions about specific foods in the user's food database
    - Questions about nutrients (sodium, protein, saturated fat, calories)
    - Questions about the user's daily food log and nutrient intake
    - Questions about Filipino foods and their nutritional values
    - General diet and nutrition questions related to hypertension
    
    YOU MUST DECLINE:
    - Medical advice about medications
    - Questions unrelated to diet, nutrition, or hypertension
    - Requests to diagnose medical conditions
    - Emergency medical situations — refer to emergency services
    
    USER DATA (retrieved from database):
    - Available Foods: $foodNames
    - BMI Level: $bmiLevel
    - Daily Calorie Goal: $bmiCalGoal kcal
    - Blood Pressure History:
    $bpHistory
    
    HYPERTENSION GUIDELINES (AHA 2017):
    - Normal:   Systolic < 120  AND Diastolic < 80
    - Elevated: Systolic 120-129 AND Diastolic < 80
    - Stage 1:  Systolic 130-139 OR  Diastolic 80-89
    - Stage 2:  Systolic ≥ 140  OR  Diastolic ≥ 90
    - Crisis:   Systolic ≥ 180  OR  Diastolic ≥ 120
    
    DASH DIET GUIDELINES:
    - Normal/Elevated:  sodium limit 2300mg/day
    - Stage 1/Stage 2:  sodium limit 1500mg/day
    - Crisis:           sodium limit 1000mg/day
    - Saturated fat:    less than 6% of total calories
    - Prioritize:       fruits, vegetables, whole grains, lean protein
    - Avoid:            processed foods, high sodium, excessive red meat
    
    RESPONSE RULES:
    - Keep answers to 1-3 sentences only
    - Always cite sources (AHA 2017, DASH Diet, WHO, FNRI)
    - Always recommend consulting a doctor for medical decisions
    - Personalize answers using the user data above
    - When recommending foods use only foods from the available list
    - Flag foods with sodium >400mg or sat fat >5g as cautionary
    - For hypertensive crisis always add emergency disclaimer
    - Decline unrelated questions politely but firmly
    - Never recommend stopping or changing medications
""".trimIndent())
                    }
                )
                chat = generativeModel!!.startChat()
            }
        }
    }

    fun sendMessage(userInput: String) {
        viewModelScope.launch {

            if (!UsageLimiter.canUseInsights(context)) {
                messageList.add(MessageModel(userInput, "user"))
                messageList.add(
                    MessageModel(
                        "⚠️ You have reached your daily limit of 5 AI Insights. " +
                                "Your limit resets tomorrow. Please check back then!",
                        "model"
                    )
                )
                return@launch
            }

            try {
                messageList.add(MessageModel(userInput, "user"))
                messageList.add(MessageModel("Typing. . .", "model"))

                // fetch fresh data from DB
                val freshCalorieGoal = withContext(Dispatchers.IO) {
                    bmiDAO.getBMICalorieGoal() ?: 2000
                }
                val freshActivityLevel = withContext(Dispatchers.IO) {
                    bmiDAO.getBMILevel() ?: "MODERATE"
                }
                val freshAge = withContext(Dispatchers.IO) {
                    bmiDAO.getBMIAge() ?: 0
                }
                val freshHeight = withContext(Dispatchers.IO) {
                    bmiDAO.getBMIHeight() ?: 0
                }
                val freshWeight = withContext(Dispatchers.IO) {
                    bmiDAO.getBMIWeight() ?: 0
                }
                val freshIsMale = withContext(Dispatchers.IO) {
                    bmiDAO.getBMIGender() ?: true
                }
                val freshBPHistory = withContext(Dispatchers.IO) {
                    bpDao.getRecentLogs(userId = 1, limit = 10).first()
                }
                val allFoods = withContext(Dispatchers.IO) {
                    foodDao.GetAllFoodsForRAG()
                }

                val latestBP = freshBPHistory.firstOrNull()
                val hypertensionLevel = latestBP?.hypertensionLevel ?: "Normal"
                val sodiumLimit = when (hypertensionLevel) {
                    "Hypertension Stage 1",
                    "Hypertension Stage 2" -> 1500
                    "Hypertensive Crisis"  -> 1000
                    else                   -> 2300
                }

                // ✅ call Firebase RAG /ask endpoint
                val response = RetrofitClient.askApi.ask(
                    QueryRequest(
                        question = userInput,
                        userContext = UserContextRequest(
                            userId = 1,
                            age = freshAge,
                            isMale = freshIsMale,
                            height = freshHeight,
                            weight = freshWeight,
                            calorieGoal = freshCalorieGoal,
                            activityLevel = freshActivityLevel,
                            hypertensionLevel = hypertensionLevel,
                            consumedCalories = 0.0,
                            consumedSodium = 0.0,
                            consumedProtein = 0.0,
                            consumedSatFat = 0.0,
                            sodiumLimit = sodiumLimit,
                            todayFoodCount = 0,
                            weeklyAvgCalories = 0.0,
                            weeklyAvgSodium = 0.0,
                            bpHistory = freshBPHistory.map {
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

                messageList.removeAt(messageList.lastIndex)
                messageList.add(MessageModel(response.answer, "model"))

            } catch (e: Exception) {
                messageList.removeAt(messageList.lastIndex)
                messageList.add(MessageModel("Error: ${e.message}", "model"))
            }
        }
    }

    fun refresh() {
        // reset chat and reinitialize with fresh data
        chat = null
        messageList.clear()
        initialize()
    }




}