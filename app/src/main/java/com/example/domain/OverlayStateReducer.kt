package com.example.domain

object OverlayStateReducer {

    fun reduce(
        serviceConnected: Boolean,
        masterEnabled: Boolean,
        imeVisible: Boolean,
        imeBounds: IntRect?,
        displayBounds: IntRect,
        safeInsets: InsetsData,
        settings: VeilSettings
    ): OverlayState {
        if (!serviceConnected || !masterEnabled || !imeVisible || imeBounds == null) {
            return OverlayState.Hidden
        }

        val validated = settings.validated()
        val band = BandGeometry.computeBand(
            displayBounds = displayBounds,
            safeInsets = safeInsets,
            imeBounds = imeBounds,
            coverageFraction = validated.coverage,
            overlapFraction = validated.overlap
        ) ?: return OverlayState.Hidden

        return OverlayState.Shown(
            band = band,
            alpha = validated.opacity
        )
    }
}
