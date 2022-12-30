package com.xdjbx.bench.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.xdjbx.bench.ui.activity.MainActivity

class LocalNotificationManager(val context: Context) {

    fun notifyMe() {
        // First, create a notification channel (required for Android 8.0 and above)
        val notificationChannelId = "notifierChannelId"
        val notificationChannelName = "IMentor Channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(notificationChannelId, notificationChannelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }

// Next, create the notification using the NotificationCompat.Builder class
        val notificationId = 123
        val notificationIntent = Intent(context, MainActivity::class.java)

        // TODO - Implement S+ Api version 31 code check
        val notificationPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        // TODO - create/select small icon
        // TODO - select title
        // TODO - select text
        // TODO - identify proper priority (conditional?)
        // TODO - possibly build sound with TextToSpeech (Take your towel)

        val notification = NotificationCompat.Builder(context, notificationChannelId)
            .setSmallIcon(androidx.core.R.drawable.notification_icon_background)
            .setContentTitle("Imentor")
            .setContentText("Take your towel")
            .setContentIntent(notificationPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSound(Settings.System.DEFAULT_ALARM_ALERT_URI)
            .setFullScreenIntent(notificationPendingIntent, true)
            .build()

// Finally, display the notification using the NotificationManager
        notificationManager.notify(notificationId, notification)
    }
}