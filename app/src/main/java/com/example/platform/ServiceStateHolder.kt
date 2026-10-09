package com.example.platform

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ServiceStateHolder {
    private val _state = MutableStateFlow<ServiceState>(ServiceState.Disconnected)
    val state: StateFlow<ServiceState> = _state.asStateFlow()

    fun update(newState: ServiceState) {
        _state.value = newState
    }
}
