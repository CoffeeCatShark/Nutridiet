package com.example.yourapp.screens

import android.R.attr.text
import android.R.id.message
import android.renderscript.ScriptGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capstone.nutridiet.viewmodels.InsightViewModel
import com.capstone.nutridiet.viewmodels.MessageModel

@Composable
fun InsightsScreen(viewModel: InsightViewModel) {
    val responseText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppHeader()
        Text(text = responseText)

        MessageList(modifier = Modifier.weight(1f).padding(16.dp)
            ,viewModel.messageList)

        MessageInput(onMessageSend = {

            viewModel.sendMessage(it)

        })

    }
}

@Composable
fun AppHeader(){
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.primary)
    ){
        Text(
            modifier = Modifier.padding(16.dp),
            text = "AI Insights")
    }
}

@Composable
fun MessageInput(onMessageSend: (String) -> Unit){

    var message by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        OutlinedTextField(value = message, onValueChange = {message = it},
            modifier = Modifier.height(60.dp)
        )
        IconButton(onClick = {
            if(message.isBlank())
                return@IconButton
            onMessageSend(message)
            message = ""
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send")}
    }

}

@Composable
fun MessageList(modifier: Modifier = Modifier,messageList: List<MessageModel>){
    LazyColumn(
        modifier = modifier, reverseLayout = true
    ) {
        items(messageList.reversed()){
            MessageRow(messageModel = it)
        }
    }
}

@Composable
fun MessageRow(messageModel: MessageModel){
    val isModel = messageModel.role=="model"

    Row(
        verticalAlignment = Alignment.CenterVertically
    ){  Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.align(
                if (isModel) Alignment.BottomStart else Alignment.BottomEnd
            )
                .padding(
                    start = if(isModel) 8.dp else 70.dp,
                    end = if(isModel) 70.dp else 8.dp,
                    top = 8.dp,
                    bottom = 8.dp
                )
                .clip(RoundedCornerShape(48f))
                .background(if(isModel) Color.LightGray else Color.Green)
                .padding(16.dp)
        ) {
            SelectionContainer {
                Text(text = messageModel.message, fontWeight = FontWeight.W500)
            }
        }
        }
    }


}