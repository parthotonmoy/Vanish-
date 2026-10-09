package com.example.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OpacityMappingTest {

    @Test
    fun defaults_and_percentages_matchSpecification() {
        val settings = VeilSettings()
        assertEquals(0.55f, settings.opacity, 0.001f)
        assertEquals(55, settings.opacityPercentage)
        assertEquals(1.00f, settings.coverage, 0.001f)
        assertEquals(100, settings.coveragePercentage)
        assertEquals(0.00f, settings.overlap, 0.001f)
        assertEquals(0, settings.overlapPercentage)
    }

    @Test
    fun validated_coercesExceededValues() {
        val outOfRangeHigh = VeilSettings(opacity = 0.95f, coverage = 1.50f, overlap = 0.90f)
        val clampedHigh = outOfRangeHigh.validated()
        assertEquals(VeilSettings.MAX_OPACITY, clampedHigh.opacity, 0.001f)
        assertEquals(VeilSettings.MAX_COVERAGE, clampedHigh.coverage, 0.001f)
        assertEquals(VeilSettings.MAX_OVERLAP, clampedHigh.overlap, 0.001f)

        val outOfRangeLow = VeilSettings(opacity = 0.02f, coverage = 0.05f, overlap = -0.10f)
        val clampedLow = outOfRangeLow.validated()
        assertEquals(VeilSettings.MIN_OPACITY, clampedLow.opacity, 0.001f)
        assertEquals(VeilSettings.MIN_COVERAGE, clampedLow.coverage, 0.001f)
        assertEquals(VeilSettings.MIN_OVERLAP, clampedLow.overlap, 0.001f)
    }

    @Test
    fun maxOpacity_cappedAt80Percent_forUntrustedTouchCompliance() {
        assertTrue(VeilSettings.MAX_OPACITY <= 0.80f)
    }
}
