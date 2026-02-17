package com.capstone.nutridiet.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nutridiet.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class InsightViewModel: ViewModel() {

    val llm : GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.GEMINI_API_KEY         ////FOLLOW PROPER GUIDELINES BEFORE PUBLISHING
    )

    val messageList by lazy { mutableStateListOf<MessageModel>() }

    fun sendMessage(question: String){
        viewModelScope.launch {
            val chat = llm.startChat()
            messageList.add(MessageModel(question,"user"))


            val response = chat.sendMessage(question)
            messageList.add(MessageModel(response.text.toString(),"model"))
            Log.i("Test",response.text.toString())
        }
    }


}