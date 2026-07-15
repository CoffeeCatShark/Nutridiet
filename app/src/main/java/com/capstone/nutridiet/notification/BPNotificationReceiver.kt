package com.capstone.nutridiet.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.capstone.nutridiet.MainActivity

class BPNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        showBPNotification(context)

        // ✅ reschedule for next day automatically
        BPNotificationScheduler.scheduleNextNotification(context)
    }

    private fun showBPNotification(context: Context) {
        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        // create notification channel (required for Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Blood Pressure Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminder to check your blood pressure"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // tap notification → opens app to DietPlan screen
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "dietplan")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Time to Check Your Blood Pressure 🩺")
            .setContentText("Log your BP reading today to keep your diet plan updated.")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Don't forget to log your blood pressure today! " +
                        "Keeping track helps personalize your diet plan."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)  // ✅ dismisses when tapped
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "bp_reminder_channel"
        const val NOTIFICATION_ID = 1001
    }
}