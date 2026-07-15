package com.capstone.nutridiet.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object BPNotificationScheduler {

    private const val REQUEST_CODE = 2001

    // ✅ schedule notification for today at the saved time
    fun scheduleNextNotification(context: Context) {
        val prefs = context.getSharedPreferences("bp_notification", Context.MODE_PRIVATE)
        val hour = prefs.getInt("notification_hour", 8)    // default 8 AM
        val minute = prefs.getInt("notification_minute", 0)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // ✅ if time already passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, BPNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // ✅ use setAndAllowWhileIdle for battery efficiency
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    // ✅ save new time and reschedule
    fun updateNotificationTime(context: Context, hour: Int, minute: Int) {
        val prefs = context.getSharedPreferences("bp_notification", Context.MODE_PRIVATE)
        prefs.edit()
            .putInt("notification_hour", hour)
            .putInt("notification_minute", minute)
            .apply()

        scheduleNextNotification(context)
    }

    // ✅ cancel notification
    fun cancelNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, BPNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    // ✅ get saved time
    fun getSavedTime(context: Context): Pair<Int, Int> {
        val prefs = context.getSharedPreferences("bp_notification", Context.MODE_PRIVATE)
        val hour = prefs.getInt("notification_hour", 8)
        val minute = prefs.getInt("notification_minute", 0)
        return Pair(hour, minute)
    }
}