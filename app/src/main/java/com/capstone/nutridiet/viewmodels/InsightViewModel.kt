package com.capstone.nutridiet.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nutridiet.BuildConfig
import com.capstone.nutridiet.database.FoodDao
import com.capstone.nutridiet.database.UserBMIDAO
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ServerException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InsightViewModel(
    private val bmiDAO: UserBMIDAO,
    private val foodDao: FoodDao
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

            withContext(Dispatchers.Main) {
                generativeModel = GenerativeModel(
                    modelName = "gemini-2.5-flash-lite",
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    systemInstruction = content {
                        text("""
            You are a clinical dietitian and nutrition expert specializing in 
            hypertension management and personalized diet planning.
            
            HYPERTENSION GUIDELINES (AHA 2017):
            - Normal:   Systolic < 120  AND Diastolic < 80
            - Elevated: Systolic 120-129 AND Diastolic < 80
            - Stage 1:  Systolic 130-139 OR  Diastolic 80-89
            - Stage 2:  Systolic ≥ 140  OR  Diastolic ≥ 90
            - Crisis:   Systolic ≥ 180  OR  Diastolic ≥ 120
            
            DASH DIET GUIDELINES:
            - Sodium:        limit to 1500-2300mg/day
            - Saturated Fat: limit to less than 6% of total calories
            - Protein:       prioritize lean sources
            - Potassium, magnesium, calcium: increase intake
            - Prioritize whole grains, fruits, vegetables, lean proteins
            - Avoid processed foods, high sodium foods, excessive red meat
            
            USER DATA:
            - Available Foods: $foodNames
            - BMI Level: $bmiLevel
            - Daily Calorie Goal: $bmiCalGoal kcal
            
            RULES:
            - Only answer questions about diet, nutrition, and hypertension
            - Decline all unrelated questions politely
            - Always recommend consulting a doctor for medical decisions
            - Keep answers short and concise — 1 to 3 sentences only
            - Always cite sources (e.g. AHA, WHO, DASH Diet guidelines)
            - Personalize answers based on the user data provided above
            - When recommending foods, use only foods from the available list above
            - Flag high sodium or high saturated fat foods as cautionary
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
}