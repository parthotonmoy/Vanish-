package com.example.domain

sealed interface OverlayState {
    data object Hidden : OverlayState

    data class Shown(
        val band: IntRect,
        val alpha: Float
    ) : OverlayState
}
