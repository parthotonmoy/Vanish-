package com.example.platform

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.PathInterpolator
import com.example.domain.IntRect
import com.example.domain.OverlayState

class OverlayController(
    private val context: Context,
    private val windowManager: WindowManager
) {
    private var overlayView: View? = null
    private var layoutParams: WindowManager.LayoutParams? = null

    private var currentBand: IntRect? = null
    private var currentAlpha: Float = 0f
    private var isFadingOut: Boolean = false

    // Fast, responsive ease-out curve (Material Decelerate / FastOutSlowIn)
    private val easeOutInterpolator = PathInterpolator(0f, 0f, 0.2f, 1f)

    fun applyState(state: OverlayState) {
        when (state) {
            is OverlayState.Hidden -> hide(immediate = false)
            is OverlayState.Shown -> show(state.band, state.alpha)
        }
    }

    private fun show(band: IntRect, alpha: Float) {
        if (band.isEmpty) {
            hide(immediate = false)
            return
        }

        val clampedAlpha = alpha.coerceIn(0.10f, 0.80f)
        val currentView = overlayView
        val isAttached = currentView != null && currentView.isAttachedToWindow

        if (currentView != null && isAttached) {
            val params = layoutParams ?: return

            // If the overlay was actively fading out, smoothly reverse the exit transition
            if (isFadingOut) {
                isFadingOut = false
                currentView.animate().cancel()

                val startAlpha = currentView.alpha
                if (startAlpha < clampedAlpha) {
                    val remainingFraction = ((clampedAlpha - startAlpha) / clampedAlpha).coerceIn(0f, 1f)
                    val reverseDuration = (150L * remainingFraction).toLong().coerceIn(30L, 150L)
                    currentView.animate()
                        .alpha(clampedAlpha)
                        .setDuration(reverseDuration)
                        .setInterpolator(easeOutInterpolator)
                        .start()
                } else {
                    currentView.alpha = clampedAlpha
                }
            } else if (currentAlpha != clampedAlpha) {
                currentView.alpha = clampedAlpha
            }

            val bandChanged = currentBand != band
            if (bandChanged) {
                params.x = band.left
                params.y = band.top
                params.width = band.width
                params.height = band.height

                try {
                    windowManager.updateViewLayout(currentView, params)
                    currentBand = band
                    Log.d("VanishOverlay", "Overlay layout updated: band=$band")
                } catch (e: Exception) {
                    Log.e("VanishOverlay", "Failed to update overlay layout", e)
                }
            }
            currentAlpha = clampedAlpha
        } else {
            // Clean up any stale or detached view before creating a fresh one
            if (currentView != null) {
                try {
                    currentView.animate().cancel()
                    windowManager.removeView(currentView)
                } catch (_: Exception) {}
            }
            isFadingOut = false

            val view = View(context).apply {
                setBackgroundColor(Color.BLACK)
                isClickable = false
                isFocusable = false
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                this.alpha = clampedAlpha
            }

            val params = WindowManager.LayoutParams(
                band.width,
                band.height,
                band.left,
                band.top,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                this.alpha = 1.0f
                title = "Vanish"

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    fitInsetsTypes = 0
                }
            }

            try {
                windowManager.addView(view, params)
                overlayView = view
                layoutParams = params
                currentBand = band
                currentAlpha = clampedAlpha
                Log.d("VanishOverlay", "Overlay window added: band=$band, alpha=$clampedAlpha")
            } catch (e: Exception) {
                Log.e("VanishOverlay", "Failed to add overlay window", e)
            }
        }
    }

    fun hide(immediate: Boolean = false) {
        val view = overlayView ?: return

        if (immediate || !view.isAttachedToWindow) {
            isFadingOut = false
            view.animate().cancel()
            removeOverlayImmediate(view)
            return
        }

        // Avoid restarting an already in-progress exit animation
        if (isFadingOut) {
            return
        }

        val startAlpha = view.alpha
        if (startAlpha <= 0.01f) {
            removeOverlayImmediate(view)
            return
        }

        isFadingOut = true
        view.animate().cancel()

        // Responsive duration of ~150 ms with smooth ease-out curve
        val duration = (150L * (startAlpha / currentAlpha.coerceAtLeast(0.1f))).toLong().coerceIn(30L, 150L)

        view.animate()
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(easeOutInterpolator)
            .withEndAction {
                // Stale callback guard: only remove if still in hiding state and same active view
                if (isFadingOut && overlayView === view) {
                    removeOverlayImmediate(view)
                }
            }
            .start()
        Log.d("VanishOverlay", "Overlay fade-out started: duration=${duration}ms")
    }

    private fun removeOverlayImmediate(view: View) {
        overlayView = null
        layoutParams = null
        currentBand = null
        currentAlpha = 0f
        isFadingOut = false

        try {
            if (view.isAttachedToWindow) {
                windowManager.removeView(view)
            }
            Log.d("VanishOverlay", "Overlay window removed cleanly")
        } catch (e: Exception) {
            try {
                windowManager.removeView(view)
            } catch (_: Exception) {}
            Log.e("VanishOverlay", "Failed to remove overlay view", e)
        }
    }

    fun release() {
        hide(immediate = true)
    }
}
