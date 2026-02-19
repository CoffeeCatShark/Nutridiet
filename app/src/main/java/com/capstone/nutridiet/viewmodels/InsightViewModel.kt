package com.capstone.nutridiet.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nutridiet.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ServerException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InsightViewModel: ViewModel() {
    val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash-lite",        //VERY LIMITED RATE USAGE
        apiKey = BuildConfig.GEMINI_API_KEY,
        systemInstruction = content {
            text(
                """
You are an LLM Designed for providing answers regarding nutrition and diet.
- Only Answer Questions Regarding Diet and Nutrition and nothing else.
- Always provide sources for every answer provided.
- Keep Your Answers Short and Concise. Limit Explanations to 1 - 3 Sentences Only.
        """.trimIndent()
            )
        }
    )


    val messageList by lazy { mutableStateListOf<MessageModel>() }


    val chat = generativeModel.startChat()

    fun sendMessage(userInput: String) {
        viewModelScope.launch {
            try {
                messageList.add(MessageModel(userInput, "user"))
                messageList.add(MessageModel("Typing. . .", "model"))
                val response = chat.sendMessage(userInput)
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



