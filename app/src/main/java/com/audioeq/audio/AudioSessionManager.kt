package com.audioeq.audio

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ActiveAudioSession(
    val sessionId: Int,
    val packageName: String,
    val appName: String,
    val isPlaying: Boolean = false
)

class AudioSessionManager(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val mediaSessionManager: MediaSessionManager? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
        } else null

    private val packageManager = context.packageManager

    private val _activeSessions = MutableStateFlow<List<ActiveAudioSession>>(emptyList())
    val activeSessions: StateFlow<List<ActiveAudioSession>> = _activeSessions

    private val _currentSession = MutableStateFlow<ActiveAudioSession?>(null)
    val currentSession: StateFlow<ActiveAudioSession?> = _currentSession

    @SuppressLint("MissingPermission")
    fun refreshActiveSessions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mediaSessionManager != null) {
            try {
                val controllers = mediaSessionManager.getActiveSessions(null)
                val sessions = controllers.mapNotNull { controller ->
                    val sessionId = controller.sessionToken?.let { token ->
                        try {
                            controller.javaClass.getMethod("getSessionId")
                                .invoke(controller) as? Int
                        } catch (_: Exception) { null }
                    } ?: 0

                    val packageName = controller.packageName ?: ""
                    val appName = try {
                        val appInfo = packageManager.getApplicationInfo(packageName, 0)
                        packageManager.getApplicationLabel(appInfo).toString()
                    } catch (_: Exception) { packageName }

                    val playbackState = controller.playbackState
                    val isPlaying = playbackState?.state == PlaybackState.STATE_PLAYING

                    if (packageName.isNotBlank() && packageName != context.packageName) {
                        ActiveAudioSession(sessionId, packageName, appName, isPlaying)
                    } else null
                }

                _activeSessions.value = sessions
                _currentSession.value = sessions.firstOrNull { it.isPlaying } ?: sessions.firstOrNull()
            } catch (_: SecurityException) {
                // Missing MEDIA_CONTENT_CONTROL permission
            }
        }
    }

    fun getAppNameForSession(sessionId: Int): String {
        return _activeSessions.value.find { it.sessionId == sessionId }?.appName ?: "Unknown"
    }

    fun requestAudioSessionFocus(): Boolean {
        val result = audioManager.requestAudioFocus(
            null,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    fun abandonAudioFocus() {
        audioManager.abandonAudioFocus(null)
    }
}
