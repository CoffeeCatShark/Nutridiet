package com.capstone.nutridiet

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.nutridiet.notification.BPNotificationScheduler

@Composable
fun NotificationSettingsScreen() {
    val context = LocalContext.current

    // load saved time
    val savedTime = BPNotificationScheduler.getSavedTime(context)
    var selectedHour by remember { mutableStateOf(savedTime.first) }
    var selectedMinute by remember { mutableStateOf(savedTime.second) }
    var isEnabled by remember { mutableStateOf(true) }
    var showConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Notification Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Set a daily reminder to check your blood pressure.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // ─── Enable/Disable Toggle ─────────────────────
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily BP Reminder",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Remind me to check blood pressure",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { enabled ->
                        isEnabled = enabled
                        if (enabled) {
                            BPNotificationScheduler.scheduleNextNotification(context)
                        } else {
                            BPNotificationScheduler.cancelNotification(context)
                        }
                    }
                )
            }
        }

        // ─── Time Picker Card ──────────────────────────
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Reminder Time",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // show current selected time
                Text(
                    text = String.format(
                        "%02d:%02d %s",
                        if (selectedHour > 12) selectedHour - 12
                        else if (selectedHour == 0) 12
                        else selectedHour,
                        selectedMinute,
                        if (selectedHour >= 12) "PM" else "AM"
                    ),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ✅ open time picker
                Button(
                    onClick = {
                        showTimePicker(
                            context = context,
                            currentHour = selectedHour,
                            currentMinute = selectedMinute,
                            onTimeSelected = { hour, minute ->
                                selectedHour = hour
                                selectedMinute = minute
                                BPNotificationScheduler.updateNotificationTime(
                                    context, hour, minute
                                )
                                showConfirmation = true
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isEnabled
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Change Time")
                }
            }
        }

        // ─── Confirmation Message ──────────────────────
        if (showConfirmation) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "✅ Reminder set for " +
                            String.format(
                                "%02d:%02d %s",
                                if (selectedHour > 12) selectedHour - 12
                                else if (selectedHour == 0) 12
                                else selectedHour,
                                selectedMinute,
                                if (selectedHour >= 12) "PM" else "AM"
                            ),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // ─── Info Card ────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ℹ️ About This Reminder",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "You will receive a daily notification at your " +
                            "chosen time to log your blood pressure. " +
                            "This helps keep your diet plan accurate and " +
                            "tracks your hypertension progress over time.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun showTimePicker(
    context: Context,
    currentHour: Int,
    currentMinute: Int,
    onTimeSelected: (Int, Int) -> Unit
) {
    TimePickerDialog(
        context,
        { _, hour, minute -> onTimeSelected(hour, minute) },
        currentHour,
        currentMinute,
        false  // false = 12-hour format, true = 24-hour format
    ).show()
}