package com.example.platform

sealed interface ServiceState {
    data object Disconnected : ServiceState
    data object Connected : ServiceState
    data class Failed(val reason: String) : ServiceState
}
