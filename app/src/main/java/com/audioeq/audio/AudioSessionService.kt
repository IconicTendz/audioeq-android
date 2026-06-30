package com.audioeq.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.audiofx.AudioEffect
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.audioeq.R

class AudioSessionService : Service() {

    private val sessionReceiver = SessionChangeReceiver()
    private var dspBridge: DspBridge? = null
    private var activeSessions = mutableMapOf<Int, AudioEffect>()

    companion object {
        const val CHANNEL_ID = "audioeq_sessions"
        const val NOTIFICATION_ID = 1002
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())

        // Register for audio session changes
        val filter = IntentFilter().apply {
            addAction(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION)
            addAction(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(sessionReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(sessionReceiver, filter)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    fun setDspBridge(bridge: DspBridge) {
        dspBridge = bridge
    }

    fun attachToSession(sessionId: Int) {
        if (activeSessions.containsKey(sessionId)) return
        try {
            val effect = AudioEffect(
                AudioEffect.EFFECT_TYPE_NULL,
                java.util.UUID.randomUUID().toString(),
                0,
                sessionId
            )
            activeSessions[sessionId] = effect
        } catch (e: Exception) {
            // Session attachment failed
        }
    }

    fun detachFromSession(sessionId: Int) {
        activeSessions.remove(sessionId)?.release()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Audio Sessions",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Monitors audio sessions for effect processing"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AudioEQ")
            .setContentText("Monitoring audio sessions")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }

    override fun onDestroy() {
        unregisterReceiver(sessionReceiver)
        activeSessions.values.forEach { it.release() }
        activeSessions.clear()
        super.onDestroy()
    }

    inner class SessionChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION -> {
                    val sessionId = intent.getIntExtra(AudioEffect.EXTRA_AUDIO_SESSION, -1)
                    if (sessionId > 0) attachToSession(sessionId)
                }
                AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION -> {
                    val sessionId = intent.getIntExtra(AudioEffect.EXTRA_AUDIO_SESSION, -1)
                    if (sessionId > 0) detachFromSession(sessionId)
                }
            }
        }
    }
}
