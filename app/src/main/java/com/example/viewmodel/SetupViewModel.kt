package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AppContainer
import com.example.platform.ServiceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SetupUiState(
    val isAccessibilityEnabled: Boolean = false,
    val isServiceConnected: Boolean = false,
    val mayRequireRestrictedSettingsNotice: Boolean = false
)

class SetupViewModel(
    private val container: AppContainer
) : ViewModel() {

    private val isAccessibilityEnabledFlow = MutableStateFlow(
        container.accessibilityStatusProvider.isAccessibilityServiceEnabled()
    )

    val uiState: StateFlow<SetupUiState> = combine(
        isAccessibilityEnabledFlow,
        container.serviceStateHolder.state
    ) { accessibilityEnabled, serviceState ->
        SetupUiState(
            isAccessibilityEnabled = accessibilityEnabled,
            isServiceConnected = serviceState is ServiceState.Connected,
            mayRequireRestrictedSettingsNotice = container.compatibilityChecker.mayRequireRestrictedSettingsNotice
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = SetupUiState(
            isAccessibilityEnabled = container.accessibilityStatusProvider.isAccessibilityServiceEnabled(),
            mayRequireRestrictedSettingsNotice = container.compatibilityChecker.mayRequireRestrictedSettingsNotice
        )
    )

    fun refreshStatus() {
        isAccessibilityEnabledFlow.value =
            container.accessibilityStatusProvider.isAccessibilityServiceEnabled()
    }

    fun dismissSetup() {
        viewModelScope.launch {
            container.settingsRepository.setSetupDismissed(true)
        }
    }
}
