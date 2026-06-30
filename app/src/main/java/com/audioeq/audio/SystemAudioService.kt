package com.audioeq.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.audiofx.AudioEffect
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.audioeq.MainActivity
import com.audioeq.R

class SystemAudioService : android.app.Service() {

    private var dspBridge: DspBridge? = null
    private var audioTrack: AudioTrack? = null
    private var processingThread: Thread? = null
    private var isProcessing = false

    companion object {
        const val CHANNEL_ID = "audioeq_processing"
        const val NOTIFICATION_ID = 1001
        const val SAMPLE_RATE = 48000
        const val BUFFER_SIZE = 256
    }

    inner class AudioBinder : android.os.Binder() {
        fun getService(): SystemAudioService = this@SystemAudioService
    }

    private val binder = AudioBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.ACTION_START -> startProcessing()
            Actions.ACTION_STOP -> stopProcessing()
            Actions.ACTION_TOGGLE -> {
                if (isProcessing) stopProcessing() else startProcessing()
            }
        }
        return START_STICKY
    }

    fun setDspBridge(bridge: DspBridge) {
        dspBridge = bridge
    }

    private fun startProcessing() {
        if (isProcessing) return
        isProcessing = true

        val bufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_FLOAT
        ).coerceAtLeast(BUFFER_SIZE * 4)

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()

        // Initialize DSP
        dspBridge?.create()
        dspBridge?.setSampleRate(SAMPLE_RATE)

        processingThread = Thread({
            val leftBuf = FloatArray(BUFFER_SIZE)
            val rightBuf = FloatArray(BUFFER_SIZE)

            while (isProcessing && audioTrack?.playState == AudioTrack.PLAYSTATE_PLAYING) {
                try {
                    // Process audio through DSP
                    dspBridge?.process(leftBuf, rightBuf, BUFFER_SIZE)

                    // Write to audio track (output)
                    val interleaved = FloatArray(BUFFER_SIZE * 2)
                    for (i in 0 until BUFFER_SIZE) {
                        interleaved[i * 2] = leftBuf[i]
                        interleaved[i * 2 + 1] = rightBuf[i]
                    }
                    audioTrack?.write(interleaved, 0, interleaved.size, AudioTrack.WRITE_BLOCKING)
                } catch (e: Exception) {
                    break
                }
            }
        }, "audioeq-processing")

        processingThread?.start()

        // Update notification
        updateNotification(true)
    }

    private fun stopProcessing() {
        isProcessing = false
        processingThread?.join(1000)
        processingThread = null
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
        dspBridge?.destroy()
        updateNotification(false)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Audio Processing",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "AudioEQ audio processing service"
                setSound(null, null)
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = PendingIntent.getService(
            this, 1,
            Intent(this, SystemAudioService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AudioEQ")
            .setContentText(if (isProcessing) "Processing audio" else "Standby")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Stop", stopIntent)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(active: Boolean) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AudioEQ")
            .setContentText(if (active) "Processing audio" else "Standby")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        stopProcessing()
        super.onDestroy()
    }

    // AudioEffect session management
    private fun registerAudioEffect() {
        val audioSessionId = audioTrack?.audioSessionId ?: return
        try {
            val effect = AudioEffect(
                AudioEffect.EFFECT_TYPE_NULL,
                AudioEffect.EFFECT_TYPE_NULL.toString(),
                0,
                audioSessionId
            )
            effect.release()
        } catch (e: Exception) {
            // Fallback: processing via audio track
        }
    }

    object Actions {
        const val ACTION_START = "com.audioeq.action.START"
        const val ACTION_STOP = "com.audioeq.action.STOP"
        const val ACTION_TOGGLE = "com.audioeq.action.TOGGLE"
    }
}

