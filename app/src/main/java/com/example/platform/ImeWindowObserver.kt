package com.example.platform

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.util.Log
import android.view.accessibility.AccessibilityWindowInfo
import com.example.domain.IntRect

object ImeBoundsValidator {
    /**
     * Verifies that window coordinates represent an actively visible on-screen keyboard
     * occupying the lower portion of the display, rather than an idle/invisible full-screen
     * window frame or an off-screen/collapsed window.
     */
    fun isVisibleKeyboard(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        displayHeight: Int
    ): Boolean {
        val w = right - left
        val h = bottom - top

        return w >= 100 &&
                h >= 120 &&
                top > (displayHeight * 0.10f) &&
                top < displayHeight &&
                bottom >= (displayHeight * 0.40f) &&
                h < (displayHeight * 0.85f)
    }
}

class ImeWindowObserver(
    private val service: AccessibilityService
) {
    private val tempRect = Rect()

    /**
     * Inspects current on-screen windows to find the active, visible IME window.
     * Extracts only coordinate bounds. Never accesses view nodes or content.
     */
    fun findImeBounds(displayHeight: Int = Int.MAX_VALUE): IntRect? {
        val windows = try {
            service.windows
        } catch (e: Exception) {
            Log.e("VeilIME", "Error accessing service.windows", e)
            return null
        }

        if (windows.isNullOrEmpty()) {
            return null
        }

        var imeBounds: IntRect? = null

        for (window in windows) {
            if (window == null) continue

            // Must be of type TYPE_INPUT_METHOD
            if (window.type == AccessibilityWindowInfo.TYPE_INPUT_METHOD) {
                tempRect.setEmpty()
                window.getBoundsInScreen(tempRect)

                val isVisible = ImeBoundsValidator.isVisibleKeyboard(
                    left = tempRect.left,
                    top = tempRect.top,
                    right = tempRect.right,
                    bottom = tempRect.bottom,
                    displayHeight = displayHeight
                )

                if (isVisible) {
                    imeBounds = IntRect(
                        left = tempRect.left,
                        top = tempRect.top,
                        right = tempRect.right,
                        bottom = tempRect.bottom
                    )
                    Log.d("VeilIME", "Active IME window confirmed: $imeBounds")
                    break
                } else {
                    Log.d("VeilIME", "IME window found but inactive/closed: $tempRect")
                }
            }
        }

        return imeBounds
    }
}
