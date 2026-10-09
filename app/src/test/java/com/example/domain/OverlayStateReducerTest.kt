package com.example.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OverlayStateReducerTest {

    private val display = IntRect(0, 0, 1080, 2400)
    private val insets = InsetsData(96, 120, 0, 0)
    private val ime = IntRect(0, 1500, 1080, 2400)
    private val settings = VeilSettings()

    @Test
    fun serviceDisconnected_outputsHidden() {
        val state = OverlayStateReducer.reduce(
            serviceConnected = false,
            masterEnabled = true,
            imeVisible = true,
            imeBounds = ime,
            displayBounds = display,
            safeInsets = insets,
            settings = settings
        )
        assertEquals(OverlayState.Hidden, state)
    }

    @Test
    fun masterDisabled_outputsHidden() {
        val state = OverlayStateReducer.reduce(
            serviceConnected = true,
            masterEnabled = false,
            imeVisible = true,
            imeBounds = ime,
            displayBounds = display,
            safeInsets = insets,
            settings = settings
        )
        assertEquals(OverlayState.Hidden, state)
    }

    @Test
    fun imeHidden_outputsHidden() {
        val state = OverlayStateReducer.reduce(
            serviceConnected = true,
            masterEnabled = true,
            imeVisible = false,
            imeBounds = null,
            displayBounds = display,
            safeInsets = insets,
            settings = settings
        )
        assertEquals(OverlayState.Hidden, state)
    }

    @Test
    fun allConditionsMet_outputsShown() {
        val state = OverlayStateReducer.reduce(
            serviceConnected = true,
            masterEnabled = true,
            imeVisible = true,
            imeBounds = ime,
            displayBounds = display,
            safeInsets = insets,
            settings = settings
        )

        assertTrue(state is OverlayState.Shown)
        val shown = state as OverlayState.Shown
        assertEquals(settings.opacity, shown.alpha, 0.001f)
        assertEquals(0, shown.band.left)
        assertEquals(1080, shown.band.right)
    }

    @Test
    fun rapidToggling_handlesTransitionsDeterministically() {
        // Step 1: Shown
        var state = OverlayStateReducer.reduce(true, true, true, ime, display, insets, settings)
        assertTrue(state is OverlayState.Shown)

        // Step 2: Keyboard hides -> Hidden
        state = OverlayStateReducer.reduce(true, true, false, null, display, insets, settings)
        assertEquals(OverlayState.Hidden, state)

        // Step 3: Keyboard re-appears -> Shown
        state = OverlayStateReducer.reduce(true, true, true, ime, display, insets, settings)
        assertTrue(state is OverlayState.Shown)

        // Step 4: Master toggle switched off during open keyboard -> immediate Hidden
        state = OverlayStateReducer.reduce(true, false, true, ime, display, insets, settings)
        assertEquals(OverlayState.Hidden, state)
    }
}
