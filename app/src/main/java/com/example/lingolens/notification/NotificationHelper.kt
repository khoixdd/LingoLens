package com.example.lingolens.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.lingolens.MainActivity
import com.example.lingolens.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun createChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannels(
            listOf(
                NotificationChannel(
                    REMINDER_CHANNEL,
                    "Learning reminders",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply { description = "Daily goal, review, and streak reminders" },
                NotificationChannel(
                    ACHIEVEMENT_CHANNEL,
                    "Achievements",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply { description = "New achievement celebrations" },
            ),
        )
    }

    fun canPostNotifications(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    fun showReminder(message: String) = show(
        id = REMINDER_NOTIFICATION_ID,
        channelId = REMINDER_CHANNEL,
        title = "LingoLens",
        message = message,
    )

    fun showAchievement(name: String) = show(
        id = ACHIEVEMENT_NOTIFICATION_BASE + name.hashCode(),
        channelId = ACHIEVEMENT_CHANNEL,
        title = "Achievement unlocked",
        message = name,
    )

    private fun show(id: Int, channelId: String, title: String, message: String) {
        if (!canPostNotifications()) return
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(context).notify(id, notification)
    }

    private companion object {
        const val REMINDER_CHANNEL = "learning_reminders"
        const val ACHIEVEMENT_CHANNEL = "achievements"
        const val REMINDER_NOTIFICATION_ID = 1001
        const val ACHIEVEMENT_NOTIFICATION_BASE = 2000
    }
}

