package com.example.data

import com.example.domain.ThemeMode
import com.example.domain.VeilSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settingsFlow: Flow<VeilSettings>

    suspend fun setEnabled(enabled: Boolean)
    suspend fun setOpacity(opacity: Float)
    suspend fun setCoverage(coverage: Float)
    suspend fun setOverlap(overlap: Float)
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setDynamicColor(enabled: Boolean)
    suspend fun setSetupDismissed(dismissed: Boolean)
    suspend fun resetAppearance()
}
