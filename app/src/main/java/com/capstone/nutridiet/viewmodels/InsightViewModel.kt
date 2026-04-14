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
                            You are an LLM designed for providing answers regarding nutrition and diet.
                            - Only answer questions regarding diet and nutrition and nothing else.
                            - Always provide sources for every answer provided.
                            - Keep your answers short and concise. Limit explanations to 1-3 sentences only.
                            - Here is the user's data to help personalize the answers:
                                - Food List: $foodNames
                                - User BMI Level: $bmiLevel
                                - User Calorie Goal: $bmiCalGoal
                            - if the user asks you to make a diet plan, tell them to use the application's built in diet plan generator and consult a dietitian
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