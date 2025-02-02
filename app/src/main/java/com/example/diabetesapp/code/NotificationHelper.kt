package com.example.diabetesapp.code

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.diabetesapp.R

/**
 * Helper class to manage notifications for the Diabetes App.
 *
 * This class handles:
 * - Creating notification channels for Android 8.0 and above.
 * - Sending notifications with permission checks for Android 13 and above.
 *
 * @property context The context in which the notifications will be sent.
 */
class NotificationHelper(private val context: Context) {

    init {
        createNotificationChannel()
    }

    /**
     * Creates a notification channel for Android 8.0 and above.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for diabetes-related notifications"
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    /**
     * Sends a notification to the user.
     *
     * @param title The title of the notification.
     * @param message The body text of the notification.
     */
    fun sendNotification(title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // FLAG_IMMUTABLE for Android 12 and above (it didn't work without it)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    companion object {
        private const val CHANNEL_ID = "diabetes_notifications"
        private const val CHANNEL_NAME = "Diabetes Notifications"
        private const val NOTIFICATION_ID = 100 // unique id for notifications
    }
}