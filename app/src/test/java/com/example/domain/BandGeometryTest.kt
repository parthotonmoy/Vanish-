package com.example.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BandGeometryTest {

    private val display = IntRect(left = 0, top = 0, right = 1080, bottom = 2400)
    private val insets = InsetsData(top = 96, bottom = 120, left = 0, right = 0)

    @Test
    fun computeBand_standardDockedKeyboard_stopsAtKeyboardTop() {
        // Standard keyboard occupying bottom from 1500 to 2400
        val ime = IntRect(left = 0, top = 1500, right = 1080, bottom = 2400)

        val band = BandGeometry.computeBand(
            displayBounds = display,
            safeInsets = insets,
            imeBounds = ime,
            coverageFraction = 1.00f, // 100% of screen above keyboard
            overlapFraction = 0.00f
        )

        assertNotNull(band)
        assertEquals(0, band!!.left)
        assertEquals(1080, band.right)
        // bandBottom must be strictly at the top of the keyboard (never covering keys)
        assertEquals(1500, band.bottom)
        // bandTop extends up to safeInsets.top
        assertEquals(96, band.top)
    }

    @Test
    fun computeBand_strictlyNeverCoversKeyboard_evenWithOverlapArgument() {
        val ime = IntRect(left = 0, top = 1600, right = 1080, bottom = 2400)

        val band = BandGeometry.computeBand(
            displayBounds = display,
            safeInsets = insets,
            imeBounds = ime,
            coverageFraction = 1.00f,
            overlapFraction = 0.20f // Even if positive overlap requested, must NOT cover keys
        )

        assertNotNull(band)
        // band bottom must NOT exceed ime.top
        assertTrue("Band bottom must never cover keyboard keys", band!!.bottom <= ime.top)
        assertEquals(1600, band.bottom)
    }

    @Test
    fun computeBand_tallKeyboardWithEmojiPanel_adaptsDynamically() {
        // Tall keyboard with emoji/toolbar expansion (top at 1100 instead of 1600)
        val tallIme = IntRect(left = 0, top = 1100, right = 1080, bottom = 2400)

        val band = BandGeometry.computeBand(
            displayBounds = display,
            safeInsets = insets,
            imeBounds = tallIme,
            coverageFraction = 1.00f
        )

        assertNotNull(band)
        assertEquals(1100, band!!.bottom)
        assertEquals(96, band.top)
    }

    @Test
    fun computeBand_landscapeOrientation_adaptsProperly() {
        val landscapeDisplay = IntRect(left = 0, top = 0, right = 2400, bottom = 1080)
        val landscapeInsets = InsetsData(top = 48, bottom = 96, left = 96, right = 96)
        val landscapeIme = IntRect(left = 0, top = 600, right = 2400, bottom = 1080)

        val band = BandGeometry.computeBand(
            displayBounds = landscapeDisplay,
            safeInsets = landscapeInsets,
            imeBounds = landscapeIme,
            coverageFraction = 1.00f
        )

        assertNotNull(band)
        assertEquals(0, band!!.left)
        assertEquals(2400, band.right)
        assertEquals(600, band.bottom)
        assertEquals(48, band.top)
    }

    @Test
    fun computeBand_partialCoverage_sitsAboveKeyboard() {
        val ime = IntRect(left = 0, top = 1500, right = 1080, bottom = 2400)

        val band = BandGeometry.computeBand(
            displayBounds = display,
            safeInsets = insets,
            imeBounds = ime,
            coverageFraction = 0.50f, // 50% of available screen above keyboard
            overlapFraction = 0.00f   // no overlap into keyboard
        )

        assertNotNull(band)
        assertEquals(1500, band!!.bottom)
        assertTrue(band.top < 1500)
        assertTrue(band.top >= insets.top)
    }

    @Test
    fun computeBand_floatingKeyboard_confinedHorizontally() {
        // Floating keyboard in the center
        val ime = IntRect(left = 200, top = 1200, right = 880, bottom = 1800)

        val band = BandGeometry.computeBand(
            displayBounds = display,
            safeInsets = insets,
            imeBounds = ime,
            coverageFraction = 0.50f,
            overlapFraction = 0.00f
        )

        assertNotNull(band)
        assertEquals(200, band!!.left)
        assertEquals(880, band.right)
        assertEquals(1200, band.bottom)
        assertTrue(band.width <= ime.width)
    }

    @Test
    fun computeBand_candidateStripBelowThreshold_returnsNull() {
        // Candidate-only strip with height 36 < 48dp threshold
        val ime = IntRect(left = 0, top = 2364, right = 1080, bottom = 2400)

        val band = BandGeometry.computeBand(
            displayBounds = display,
            safeInsets = insets,
            imeBounds = ime,
            coverageFraction = 1.00f,
            overlapFraction = 0.00f
        )

        assertNull(band)
    }

    @Test
    fun computeBand_emptyOrNullBounds_returnsNull() {
        val emptyIme = IntRect(0, 0, 0, 0)
        assertNull(BandGeometry.computeBand(display, insets, null, 1.00f, 0.00f))
        assertNull(BandGeometry.computeBand(display, insets, emptyIme, 1.00f, 0.00f))
        assertNull(BandGeometry.computeBand(IntRect(0, 0, 0, 0), insets, display, 1.00f, 0.00f))
    }
}
