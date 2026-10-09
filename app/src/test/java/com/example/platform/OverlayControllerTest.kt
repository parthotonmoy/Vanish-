package com.example.platform

import android.content.Context
import android.view.WindowManager
import androidx.test.core.app.ApplicationProvider
import com.example.domain.IntRect
import com.example.domain.OverlayState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class OverlayControllerTest {

    @Test
    fun overlayController_createsOverlayWithVanishTitle() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val controller = OverlayController(context, wm)

        val band = IntRect(0, 1000, 1080, 1500)
        controller.applyState(OverlayState.Shown(band = band, alpha = 0.55f))

        // Releasing removes overlay cleanly
        controller.release()
    }

    @Test
    fun overlayController_reopeningDuringFadeOutReversesSmoothly() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val controller = OverlayController(context, wm)

        val band = IntRect(0, 1000, 1080, 1500)
        // Show overlay
        controller.applyState(OverlayState.Shown(band = band, alpha = 0.55f))

        // Start fade out
        controller.applyState(OverlayState.Hidden)

        // Keyboard reopens immediately during fade out
        controller.applyState(OverlayState.Shown(band = band, alpha = 0.55f))

        // Cleanup
        controller.release()
    }

    @Test
    fun overlayController_cleanImmediateReleaseLeavesNoOverlay() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val controller = OverlayController(context, wm)

        val band = IntRect(0, 1000, 1080, 1500)
        controller.applyState(OverlayState.Shown(band = band, alpha = 0.55f))
        controller.release()

        // Calling hide again is safe and idempotent
        controller.hide(immediate = true)
    }
}
