package com.capstone.nutridiet.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nutridiet.BuildConfig
import com.capstone.nutridiet.database.BloodPressureDao
import com.capstone.nutridiet.database.FoodDao
import com.capstone.nutridiet.database.UserBMIDAO
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ServerException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InsightViewModel(
    private val bmiDAO: UserBMIDAO,
    private val foodDao: FoodDao,
    private val bpDao: BloodPressureDao
) : ViewModel() {

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
            // ✅ guard if model not initialized yet
            if (chat == null) {
                messageList.add(MessageModel("Still loading, please wait a moment.", "model"))
                return@launch
            }
            try {
                messageList.add(MessageModel(userInput, "user"))
                messageList.add(MessageModel("Typing. . .", "model"))
                val response = chat!!.sendMessage(userInput)
                messageList.removeAt(messageList.lastIndex)

                val reply = response.text
                withContext(Dispatchers.Main) {
                    messageList.add(MessageModel(reply.toString(), "model"))
                }
            } catch (e: ServerException) {
                messageList.removeAt(messageList.lastIndex)
                messageList.add(MessageModel("Error: " + e.message.toString(), "model"))
            } catch (e: Exception) {
                messageList.removeAt(messageList.lastIndex)
                messageList.add(MessageModel("Error: " + e.message.toString(), "model"))
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