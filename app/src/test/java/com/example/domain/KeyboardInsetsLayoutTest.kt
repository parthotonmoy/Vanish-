package com.example.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyboardInsetsLayoutTest {

    private val display = IntRect(0, 0, 1080, 2400)
    private val systemInsets = InsetsData(top = 96, bottom = 120, left = 0, right = 0)

    @Test
    fun dynamicContentArea_recalculatesOnImeAppear() {
        // Closed state: viewport is full display minus system bars
        val availableHeightClosed = display.height - systemInsets.top - systemInsets.bottom
        assertEquals(2400 - 96 - 120, availableHeightClosed)

        // Keyboard opens with 800px height
        val imeHeight = 800
        val ime = IntRect(0, display.height - imeHeight, display.width, display.height)
        val band = BandGeometry.computeBand(
            displayBounds = display,
            safeInsets = systemInsets,
            imeBounds = ime,
            coverageFraction = 1.0f
        )

        assertNotNull(band)
        // Content area above keyboard is top to ime.top
        val availableContentHeight = ime.top - systemInsets.top
        assertEquals(2400 - 800 - 96, availableContentHeight)
        // Veil band stops exactly at keyboard top, leaving keyboard 100% uncovered
        assertEquals(ime.top, band!!.bottom)
    }

    @Test
    fun dynamicContentArea_adaptsToImeResize() {
        // Keyboard opens initially at 750px
        val imeInitial = IntRect(0, 2400 - 750, 1080, 2400)
        val bandInitial = BandGeometry.computeBand(display, systemInsets, imeInitial, 1.0f)
        assertEquals(1650, bandInitial!!.bottom)

        // User opens emoji picker -> keyboard expands to 1050px
        val imeExpanded = IntRect(0, 2400 - 1050, 1080, 2400)
        val bandExpanded = BandGeometry.computeBand(display, systemInsets, imeExpanded, 1.0f)
        assertEquals(1350, bandExpanded!!.bottom)
        assertEquals(96, bandExpanded.top)

        // Veil adapted height without touching keyboard
        assertTrue(bandExpanded.bottom < bandInitial.bottom)
    }

    @Test
    fun dynamicContentArea_restoresWhenImeCloses() {
        // When IME is closed / null
        val bandClosed = BandGeometry.computeBand(display, systemInsets, null, 1.0f)
        assertEquals(null, bandClosed)

        // OverlayStateReducer outputs Hidden
        val state = OverlayStateReducer.reduce(
            serviceConnected = true,
            masterEnabled = true,
            imeVisible = false,
            imeBounds = null,
            displayBounds = display,
            safeInsets = systemInsets,
            settings = VeilSettings()
        )
        assertEquals(OverlayState.Hidden, state)
    }
}
