package com.audioeq.ui.components

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.audioeq.R
import com.audioeq.audio.SystemAudioService

class AudioEqTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        val intent = Intent(this, SystemAudioService::class.java).apply {
            action = SystemAudioService.Actions.ACTION_TOGGLE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        updateTile()
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        tile.apply {
            state = when {
                isActive() -> Tile.STATE_ACTIVE
                else -> Tile.STATE_INACTIVE
            }
            label = "AudioEQ"
            icon = androidx.core.graphics.drawable.IconCompat.createWithResource(
                this@AudioEqTileService,
                R.drawable.ic_quick_tile
            ).toIcon(this@AudioEqTileService)
        }
        tile.updateTile()
    }

    private fun isActive(): Boolean = false
}
