package com.example.platform

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import com.example.VeilApplication
import com.example.domain.InsetsData
import com.example.domain.IntRect
import com.example.domain.OverlayState
import com.example.domain.OverlayStateReducer
import com.example.domain.VeilSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class VeilAccessibilityService : AccessibilityService() {

    private var serviceScope: CoroutineScope? = null
    private var overlayController: OverlayController? = null
    private var imeObserver: ImeWindowObserver? = null

    private var currentSettings: VeilSettings = VeilSettings()
    private var cachedDisplayBounds: IntRect = IntRect.Empty
    private var cachedSafeInsets: InsetsData = InsetsData.Zero

    private var isOverlayShowing = false
    private var watchdogJob: Job? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("VeilService", "Accessibility service connected")

        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        overlayController = OverlayController(this, wm)
        imeObserver = ImeWindowObserver(this)

        updateDisplayGeometry()

        val app = application as? VeilApplication
        app?.container?.serviceStateHolder?.update(ServiceState.Connected)

        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        serviceScope = scope

        // Collect persistent settings
        scope.launch {
            app?.container?.settingsRepository?.settingsFlow?.collectLatest { settings ->
                currentSettings = settings
                if (!settings.enabled) {
                    overlayController?.hide(immediate = true)
                    isOverlayShowing = false
                    stopWatchdog()
                } else {
                    evaluateImeState()
                }
            }
        }

        evaluateImeState()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!currentSettings.enabled) {
            overlayController?.hide(immediate = true)
            isOverlayShowing = false
            stopWatchdog()
            return
        }

        // Re-evaluate IME state on any window, focus, or interaction event
        evaluateImeState()
    }

    override fun onInterrupt() {
        Log.d("VeilService", "Accessibility service interrupted")
        overlayController?.hide(immediate = true)
        isOverlayShowing = false
        stopWatchdog()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateDisplayGeometry()
        evaluateImeState()
    }

    private fun evaluateImeState() {
        val displayH = if (cachedDisplayBounds.height > 0) cachedDisplayBounds.height else resources.displayMetrics.heightPixels
        val imeBounds = imeObserver?.findImeBounds(displayH)
        val imeVisible = imeBounds != null && !imeBounds.isEmpty

        val state = OverlayStateReducer.reduce(
            serviceConnected = true,
            masterEnabled = currentSettings.enabled,
            imeVisible = imeVisible,
            imeBounds = imeBounds,
            displayBounds = cachedDisplayBounds,
            safeInsets = cachedSafeInsets,
            settings = currentSettings
        )

        overlayController?.applyState(state)

        if (state is OverlayState.Shown) {
            isOverlayShowing = true
            startWatchdogIfNeeded()
        } else {
            isOverlayShowing = false
            stopWatchdog()
        }
    }

    private fun startWatchdogIfNeeded() {
        if (watchdogJob?.isActive == true) return
        watchdogJob = serviceScope?.launch {
            while (isOverlayShowing && isActive) {
                delay(40L)
                evaluateImeState()
            }
        }
    }

    private fun stopWatchdog() {
        watchdogJob?.cancel()
        watchdogJob = null
    }

    private fun updateDisplayGeometry() {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = resources.displayMetrics
        var width = dm.widthPixels
        var height = dm.heightPixels

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val metrics = wm.currentWindowMetrics
                val bounds = metrics.bounds
                if (bounds.width() > 0 && bounds.height() > 0) {
                    width = bounds.width()
                    height = bounds.height()
                }

                val windowInsets = metrics.windowInsets
                val insets = windowInsets.getInsetsIgnoringVisibility(
                    WindowInsets.Type.statusBars() or
                            WindowInsets.Type.displayCutout() or
                            WindowInsets.Type.navigationBars()
                )
                cachedSafeInsets = InsetsData(
                    top = insets.top,
                    bottom = insets.bottom,
                    left = insets.left,
                    right = insets.right
                )
            } catch (e: Exception) {
                Log.e("VeilService", "Error reading window metrics", e)
                cachedSafeInsets = InsetsData.Zero
            }
        } else {
            cachedSafeInsets = InsetsData(top = 0, bottom = 0, left = 0, right = 0)
        }

        cachedDisplayBounds = IntRect(
            left = 0,
            top = 0,
            right = width,
            bottom = height
        )
        Log.d("VeilService", "Display geometry updated: $cachedDisplayBounds, insets: $cachedSafeInsets")
    }

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        Log.d("VeilService", "Accessibility service unbound")
        cleanupService()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d("VeilService", "Accessibility service destroyed")
        cleanupService()
        super.onDestroy()
    }

    private fun cleanupService() {
        stopWatchdog()
        overlayController?.release()
        overlayController = null
        imeObserver = null

        val app = application as? VeilApplication
        app?.container?.serviceStateHolder?.update(ServiceState.Disconnected)

        serviceScope?.cancel()
        serviceScope = null
    }
}
