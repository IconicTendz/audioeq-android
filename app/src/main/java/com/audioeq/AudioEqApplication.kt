package com.audioeq

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.audioeq.data.db.AudioEqDatabase

class AudioEqApplication : Application() {

    lateinit var database: AudioEqDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AudioEqDatabase.getInstance(this)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            val audioChannel = NotificationChannel(
                CHANNEL_AUDIO,
                "Audio Processing",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows when audio processing is active"
                setShowBadge(false)
                enableVibration(false)
                enableLights(false)
            }

            val backupChannel = NotificationChannel(
                CHANNEL_BACKUP,
                "Cloud Backup",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Cloud backup notifications"
                setShowBadge(false)
            }

            notificationManager.createNotificationChannel(audioChannel)
            notificationManager.createNotificationChannel(backupChannel)
        }
    }

    companion object {
        const val CHANNEL_AUDIO = "audio_processing"
        const val CHANNEL_BACKUP = "cloud_backup"

        lateinit var instance: AudioEqApplication
            private set
    }
}
