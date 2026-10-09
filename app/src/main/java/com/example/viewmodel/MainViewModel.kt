package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AppContainer
import com.example.domain.VeilSettings
import com.example.platform.ServiceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MainUiState(
    val settings: VeilSettings = VeilSettings(),
    val serviceState: ServiceState = ServiceState.Disconnected,
    val isAccessibilityEnabled: Boolean = false
) {
    val isServiceConnected: Boolean
        get() = serviceState is ServiceState.Connected

    val needsSetup: Boolean
        get() = !isAccessibilityEnabled || !isServiceConnected
}

class MainViewModel(
    private val container: AppContainer
) : ViewModel() {

    private val isAccessibilityEnabledFlow = MutableStateFlow(
        container.accessibilityStatusProvider.isAccessibilityServiceEnabled()
    )

    val uiState: StateFlow<MainUiState> = combine(
        container.settingsRepository.settingsFlow,
        container.serviceStateHolder.state,
        isAccessibilityEnabledFlow
    ) { settings, serviceState, accessibilityEnabled ->
        MainUiState(
            settings = settings,
            serviceState = serviceState,
            isAccessibilityEnabled = accessibilityEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = MainUiState(
            isAccessibilityEnabled = container.accessibilityStatusProvider.isAccessibilityServiceEnabled()
        )
    )

    fun refreshStatus() {
        isAccessibilityEnabledFlow.value =
            container.accessibilityStatusProvider.isAccessibilityServiceEnabled()
    }

    fun setMasterEnabled(enabled: Boolean) {
        viewModelScope.launch {
            container.settingsRepository.setEnabled(enabled)
        }
    }

    fun setOpacity(opacity: Float) {
        viewModelScope.launch {
            container.settingsRepository.setOpacity(opacity)
        }
    }

    fun setCoverage(coverage: Float) {
        viewModelScope.launch {
            container.settingsRepository.setCoverage(coverage)
        }
    }

    fun setOverlap(overlap: Float) {
        viewModelScope.launch {
            container.settingsRepository.setOverlap(overlap)
        }
    }
}
