package com.audioeq.audio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.util.Log

class AudioSessionReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "AudioSessionReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION -> {
                val sessionId = intent.getIntExtra(AudioEffect.EXTRA_AUDIO_SESSION, -1)
                val packageName = intent.getStringExtra(AudioEffect.EXTRA_PACKAGE_NAME) ?: "unknown"
                Log.d(TAG, "Audio session opened: $sessionId from $packageName")
            }
            AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION -> {
                val sessionId = intent.getIntExtra(AudioEffect.EXTRA_AUDIO_SESSION, -1)
                Log.d(TAG, "Audio session closed: $sessionId")
            }
        }
    }
}
