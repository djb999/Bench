package com.xdjbx.bench.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.app.NotificationCompat
import com.xdjbx.bench.ui.activity.NotifierActivity
import java.io.File

class LocalNotificationManager(val context: Context): TextToSpeech.OnInitListener {

    var message = ""
    private lateinit var tts: TextToSpeech

    fun notifyMe(message: String) {
        tts = TextToSpeech(context, this)
        this.message = message
    }

    fun sendNotification(notificationSoundUri: Uri) {
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

        // TODO - move string management

// Next, create the notification using the NotificationCompat.Builder class
        val notificationId = 123
        val notificationIntent = Intent(context, NotifierActivity::class.java)
        notificationIntent.putExtra(messageKey, message)

        // TODO - Implement S+ Api version 31 code check
        val notificationPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)

        // TODO - create/select small icon
        // TODO - select title
        // TODO - select text
        // TODO - identify proper priority (conditional?)
        // TODO - possibly build sound with TextToSpeech (Take your towel)


        val notification = NotificationCompat.Builder(context, notificationChannelId)
            .setSmallIcon(androidx.core.R.drawable.notification_icon_background)
            .setContentTitle("Imentor")
            .setContentText(message)
            .setContentIntent(notificationPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSound(notificationSoundUri)
            .setFullScreenIntent(notificationPendingIntent, true)
            .build()

// Finally, display the notification using the NotificationManager
        notificationManager.notify(notificationId, notification)
//            .setSound(Settings.System.DEFAULT_ALARM_ALERT_URI)

    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set the text to be spoken
            val result = tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
            if (result == TextToSpeech.ERROR) {
                Log.e("TAG", "Error speaking text")
            } else {
                // Create a file to store the spoken audio
                val file = File.createTempFile("notification_sound", ".wav", context.cacheDir)
                // Save the audio to the file
                tts.synthesizeToFile(message, null, file, null)
                // Create a Uri for the file
                val soundUri = Uri.fromFile(file)
                sendNotification(soundUri)
            }
        } else {
            Log.e("TAG", "Error initializing TextToSpeech")
        }
    }

    companion object {
        val messageKey = "notificationText"
    }
}