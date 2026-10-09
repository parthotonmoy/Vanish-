package com.example.platform

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ImeBoundsValidatorTest {

    private val displayHeightPortrait = 2400
    private val displayWidthPortrait = 1080

    @Test
    fun isVisibleKeyboard_standardDockedPortrait_returnsTrue() {
        // Active keyboard occupying lower portion of display (top = 1500, bottom = 2400)
        val visible = ImeBoundsValidator.isVisibleKeyboard(
            left = 0,
            top = 1500,
            right = displayWidthPortrait,
            bottom = 2400,
            displayHeight = displayHeightPortrait
        )
        assertTrue("Standard active keyboard should be recognized as visible", visible)
    }

    @Test
    fun isVisibleKeyboard_floatingKeyboardInLowerHalf_returnsTrue() {
        val visible = ImeBoundsValidator.isVisibleKeyboard(
            left = 200,
            top = 1300,
            right = 880,
            bottom = 1900,
            displayHeight = displayHeightPortrait
        )
        assertTrue("Active floating keyboard in lower half should be recognized as visible", visible)
    }

    @Test
    fun isVisibleKeyboard_landscapeOrientation_returnsTrue() {
        val landscapeHeight = 1080
        val visible = ImeBoundsValidator.isVisibleKeyboard(
            left = 0,
            top = 500,
            right = 2400,
            bottom = landscapeHeight,
            displayHeight = landscapeHeight
        )
        assertTrue("Active landscape keyboard should be recognized as visible", visible)
    }

    @Test
    fun isVisibleKeyboard_idleFullScreenWindow_returnsFalse() {
        // Critical bug fix: when keyboard is dismissed or on home screen, IME window frame can be full-screen
        val visible = ImeBoundsValidator.isVisibleKeyboard(
            left = 0,
            top = 0,
            right = displayWidthPortrait,
            bottom = displayHeightPortrait,
            displayHeight = displayHeightPortrait
        )
        assertFalse("Full-screen idle/invisible IME window must NOT be recognized as visible keyboard", visible)
    }

    @Test
    fun isVisibleKeyboard_offScreenWindow_returnsFalse() {
        val visible = ImeBoundsValidator.isVisibleKeyboard(
            left = 0,
            top = 2400,
            right = displayWidthPortrait,
            bottom = 3200,
            displayHeight = displayHeightPortrait
        )
        assertFalse("Off-screen window must be rejected", visible)
    }

    @Test
    fun isVisibleKeyboard_emptyBounds_returnsFalse() {
        val visible = ImeBoundsValidator.isVisibleKeyboard(
            left = 0,
            top = 0,
            right = 0,
            bottom = 0,
            displayHeight = displayHeightPortrait
        )
        assertFalse("Empty bounds must be rejected", visible)
    }

    @Test
    fun isVisibleKeyboard_thinCandidateStripBelowHeightThreshold_returnsFalse() {
        // Strip of height 40 < 120 threshold
        val visible = ImeBoundsValidator.isVisibleKeyboard(
            left = 0,
            top = 2360,
            right = displayWidthPortrait,
            bottom = 2400,
            displayHeight = displayHeightPortrait
        )
        assertFalse("Candidate strip below height threshold must be rejected", visible)
    }

    @Test
    fun isVisibleKeyboard_topAlignedWindow_returnsFalse() {
        // Window starting at top = 0
        val visible = ImeBoundsValidator.isVisibleKeyboard(
            left = 0,
            top = 0,
            right = displayWidthPortrait,
            bottom = 800,
            displayHeight = displayHeightPortrait
        )
        assertFalse("Window starting at top of screen is not a keyboard", visible)
    }
}
