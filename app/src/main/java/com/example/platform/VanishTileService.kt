package com.example.platform

import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import com.example.R
import com.example.VeilApplication
import com.example.data.DataStoreSettingsRepository
import com.example.data.SettingsRepository
import com.example.data.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VanishTileService : TileService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var listeningJob: Job? = null

    private val settingsRepository: SettingsRepository by lazy {
        (application as? VeilApplication)?.container?.settingsRepository
            ?: DataStoreSettingsRepository(applicationContext.dataStore)
    }

    override fun onStartListening() {
        super.onStartListening()
        listeningJob?.cancel()
        listeningJob = serviceScope.launch {
            settingsRepository.settingsFlow.collectLatest { settings ->
                updateTileState(settings.enabled)
            }
        }
    }

    override fun onStopListening() {
        listeningJob?.cancel()
        listeningJob = null
        super.onStopListening()
    }

    override fun onClick() {
        super.onClick()
        val currentActive = (qsTile?.state == Tile.STATE_ACTIVE)
        val nextState = !currentActive

        // Optimistically update tile immediately for instant tactile feedback
        updateTileState(nextState)

        serviceScope.launch {
            try {
                settingsRepository.setEnabled(nextState)
                Log.d(TAG, "Quick Settings tile toggled enabled to: $nextState")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to persist enabled state from tile click", e)
                // Revert to actual state on error
                updateTileState(currentActive)
            }
        }
    }

    private fun updateTileState(enabled: Boolean) {
        val tile = qsTile ?: return
        tile.state = if (enabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = getString(R.string.quick_settings_tile_label)
        tile.icon = Icon.createWithResource(this, R.drawable.ic_quick_settings_tile)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = if (enabled) {
                getString(R.string.quick_settings_tile_subtitle_on)
            } else {
                getString(R.string.quick_settings_tile_subtitle_off)
            }
        }

        tile.contentDescription = if (enabled) {
            getString(R.string.quick_settings_tile_desc_active)
        } else {
            getString(R.string.quick_settings_tile_desc_inactive)
        }

        tile.updateTile()
    }

    override fun onDestroy() {
        listeningJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "VanishTileService"

        /**
         * Requests the system to refresh the Quick Settings tile state if visible.
         */
        fun requestTileUpdate(context: Context) {
            try {
                requestListeningState(
                    context,
                    ComponentName(context, VanishTileService::class.java)
                )
            } catch (e: Exception) {
                Log.w(TAG, "Unable to request tile listening state update", e)
            }
        }
    }
}
