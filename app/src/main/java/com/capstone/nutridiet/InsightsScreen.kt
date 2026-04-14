package com.example.yourapp.screens

import android.R
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        AppHeader()

        // Message list takes all remaining space
        MessageList(
            modifier = Modifier
                .weight(1f)  // ✅ pushes input to bottom
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            messageList = viewModel.messageList
        )

        // Input always stays at the bottom
        HorizontalDivider()
        MessageInput(
            onMessageSend = { viewModel.sendMessage(it) }
        )
    }
}

@Composable
fun MessageInput(onMessageSend: (String) -> Unit) {
    var message by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp),  // ✅ heightIn instead of fixed height
            placeholder = { Text("Ask something...") }
        )
        IconButton(onClick = {
            if (message.isBlank()) return@IconButton
            onMessageSend(message)
            message = ""
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send"
            )
        }
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
            text = "AI Insights",
            color = Color.White)
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