package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.ThemeMode
import com.example.domain.VeilSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "veil_settings")

class DataStoreSettingsRepository(
    private val dataStore: DataStore<Preferences>,
    private val context: Context? = null
) : SettingsRepository {

    private object PreferencesKeys {
        val ENABLED = booleanPreferencesKey("veil_master_enabled")
        val OPACITY = floatPreferencesKey("veil_opacity")
        val COVERAGE = floatPreferencesKey("veil_coverage")
        val OVERLAP = floatPreferencesKey("veil_overlap")
        val THEME_MODE = stringPreferencesKey("veil_theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("veil_dynamic_color")
        val SETUP_DISMISSED = booleanPreferencesKey("veil_setup_dismissed")
    }

    override val settingsFlow: Flow<VeilSettings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val enabled = preferences[PreferencesKeys.ENABLED] ?: true
            val opacity = preferences[PreferencesKeys.OPACITY] ?: VeilSettings.DEFAULT_OPACITY
            val coverage = preferences[PreferencesKeys.COVERAGE] ?: VeilSettings.DEFAULT_COVERAGE
            val overlap = preferences[PreferencesKeys.OVERLAP] ?: VeilSettings.DEFAULT_OVERLAP
            val themeModeStr = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            val dynamicColor = preferences[PreferencesKeys.DYNAMIC_COLOR] ?: false
            val setupDismissed = preferences[PreferencesKeys.SETUP_DISMISSED] ?: false

            val themeMode = try {
                ThemeMode.valueOf(themeModeStr)
            } catch (_: IllegalArgumentException) {
                ThemeMode.SYSTEM
            }

            VeilSettings(
                enabled = enabled,
                opacity = opacity,
                coverage = coverage,
                overlap = overlap,
                themeMode = themeMode,
                dynamicColor = dynamicColor,
                setupDismissed = setupDismissed
            ).validated()
        }

    override suspend fun setEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ENABLED] = enabled
        }
        context?.let { ctx ->
            com.example.platform.VanishTileService.requestTileUpdate(ctx)
        }
    }

    override suspend fun setOpacity(opacity: Float) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.OPACITY] = opacity.coerceIn(
                VeilSettings.MIN_OPACITY,
                VeilSettings.MAX_OPACITY
            )
        }
    }

    override suspend fun setCoverage(coverage: Float) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.COVERAGE] = coverage.coerceIn(
                VeilSettings.MIN_COVERAGE,
                VeilSettings.MAX_COVERAGE
            )
        }
    }

    override suspend fun setOverlap(overlap: Float) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.OVERLAP] = overlap.coerceIn(
                VeilSettings.MIN_OVERLAP,
                VeilSettings.MAX_OVERLAP
            )
        }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    override suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DYNAMIC_COLOR] = enabled
        }
    }

    override suspend fun setSetupDismissed(dismissed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SETUP_DISMISSED] = dismissed
        }
    }

    override suspend fun resetAppearance() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.OPACITY] = VeilSettings.DEFAULT_OPACITY
            preferences[PreferencesKeys.COVERAGE] = VeilSettings.DEFAULT_COVERAGE
            preferences[PreferencesKeys.OVERLAP] = VeilSettings.DEFAULT_OVERLAP
        }
    }
}
