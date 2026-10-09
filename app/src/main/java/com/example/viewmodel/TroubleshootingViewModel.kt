package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.AppContainer
import com.example.platform.ServiceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TroubleshootingUiState(
    val isAccessibilityEnabled: Boolean = false,
    val isServiceConnected: Boolean = false,
    val testInputText: String = ""
)

class TroubleshootingViewModel(
    private val container: AppContainer
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TroubleshootingUiState(
            isAccessibilityEnabled = container.accessibilityStatusProvider.isAccessibilityServiceEnabled(),
            isServiceConnected = container.serviceStateHolder.state.value is ServiceState.Connected
        )
    )
    val uiState: StateFlow<TroubleshootingUiState> = _uiState.asStateFlow()

    fun updateTestInput(text: String) {
        _uiState.value = _uiState.value.copy(testInputText = text)
    }

    fun refreshStatus() {
        _uiState.value = _uiState.value.copy(
            isAccessibilityEnabled = container.accessibilityStatusProvider.isAccessibilityServiceEnabled(),
            isServiceConnected = container.serviceStateHolder.state.value is ServiceState.Connected
        )
    }
}
