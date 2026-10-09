package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AppContainer
import com.example.domain.ThemeMode
import com.example.domain.VeilSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val settings: VeilSettings = VeilSettings(),
    val showResetDialog: Boolean = false,
    val supportsDynamicColor: Boolean = false
)

class SettingsViewModel(
    private val container: AppContainer
) : ViewModel() {

    private val showResetDialogFlow = MutableStateFlow(false)

    val uiState: StateFlow<SettingsUiState> = combine(
        container.settingsRepository.settingsFlow,
        showResetDialogFlow
    ) { settings, showReset ->
        SettingsUiState(
            settings = settings,
            showResetDialog = showReset,
            supportsDynamicColor = container.compatibilityChecker.supportsDynamicColor
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = SettingsUiState(
            supportsDynamicColor = container.compatibilityChecker.supportsDynamicColor
        )
    )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            container.settingsRepository.setThemeMode(mode)
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            container.settingsRepository.setDynamicColor(enabled)
        }
    }

    fun showResetDialog(show: Boolean) {
        showResetDialogFlow.value = show
    }

    fun resetAppearance() {
        viewModelScope.launch {
            container.settingsRepository.resetAppearance()
            showResetDialogFlow.value = false
        }
    }
}
