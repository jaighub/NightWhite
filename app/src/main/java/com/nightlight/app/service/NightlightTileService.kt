package com.nightlight.app.service

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class NightlightTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        val prefs = getSharedPreferences("nightlight_prefs", MODE_PRIVATE)
        val isOn = prefs.getBoolean("isPoweredOn", false)
        prefs.edit().putBoolean("isPoweredOn", !isOn).apply()
        updateTile()
    }

    private fun updateTile() {
        val prefs = getSharedPreferences("nightlight_prefs", MODE_PRIVATE)
        val isOn = prefs.getBoolean("isPoweredOn", false)
        qsTile?.apply {
            state = if (isOn) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = "Nightlight"
            updateTile()
        }
    }
}
