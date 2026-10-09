package com.example.domain

object BandGeometry {

    /**
     * Computes the privacy band rectangle in display coordinates.
     * - The band covers the screen ABOVE the keyboard.
     * - Does NOT cover the keyboard keys (bandBottom = imeBounds.top).
     * - Upper edge extends upward based on coverage fraction (up to safeInsets.top).
     * - Docked keyboards span display width; floating keyboards confine horizontally to IME bounds.
     */
    fun computeBand(
        displayBounds: IntRect,
        safeInsets: InsetsData,
        imeBounds: IntRect?,
        coverageFraction: Float,
        overlapFraction: Float = 0.0f
    ): IntRect? {
        if (imeBounds == null || imeBounds.isEmpty || displayBounds.isEmpty) {
            return null
        }

        val imeHeight = imeBounds.height
        val displayHeight = displayBounds.height
        val displayWidth = displayBounds.width

        // Reject non-keyboard windows or thin candidate strips
        if (imeHeight < 48 || imeBounds.width < 50) {
            return null
        }

        val isDocked = imeBounds.width >= (displayWidth * 0.85f)

        val bandLeft: Int
        val bandRight: Int

        if (isDocked) {
            bandLeft = 0
            bandRight = displayWidth
        } else {
            bandLeft = imeBounds.left
            bandRight = imeBounds.right
        }

        // Lower edge: sits strictly at the top of the keyboard.
        // It NEVER covers the keyboard keys or enters the keyboard area.
        val bandBottom = imeBounds.top.coerceIn(0, displayHeight)

        // Available screen height above the keyboard
        val screenAboveKeyboard = (imeBounds.top - safeInsets.top).coerceAtLeast(0)
        if (screenAboveKeyboard <= 0) {
            return null
        }

        // Band height: covers screen above keyboard according to coverage fraction
        val desiredHeight = if (coverageFraction >= 0.95f) {
            screenAboveKeyboard
        } else {
            Math.round(coverageFraction.coerceIn(0.10f, 1.0f) * screenAboveKeyboard)
        }

        val bandTop = (bandBottom - desiredHeight).coerceAtLeast(safeInsets.top)

        if (bandBottom <= bandTop || bandRight <= bandLeft) {
            return null
        }

        return IntRect(
            left = bandLeft,
            top = bandTop,
            right = bandRight,
            bottom = bandBottom
        )
    }
}
