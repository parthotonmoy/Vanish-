package com.example.platform

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.AppContainer
import com.example.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class VanishTileServiceTest {

    @Test
    fun manifest_declaresQuickSettingsTileCorrectly() {
        val candidates = listOf(
            File("src/main/AndroidManifest.xml"),
            File("app/src/main/AndroidManifest.xml")
        )
        val file = candidates.firstOrNull { it.exists() }
            ?: throw IllegalStateException("AndroidManifest.xml not found")
        val content = file.readText()

        assertTrue(
            "Manifest must declare VanishTileService",
            content.contains("android:name=\".platform.VanishTileService\"")
        )
        assertTrue(
            "TileService must require BIND_QUICK_SETTINGS_TILE permission",
            content.contains("android:permission=\"android.permission.BIND_QUICK_SETTINGS_TILE\"")
        )
        assertTrue(
            "TileService must be exported",
            content.contains("android:exported=\"true\"")
        )
        assertTrue(
            "TileService must register action QS_TILE",
            content.contains("android.service.quicksettings.action.QS_TILE")
        )
    }

    @Test
    fun tileResources_existAndAreNonEmpty() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val label = context.getString(R.string.quick_settings_tile_label)
        val subtitleOn = context.getString(R.string.quick_settings_tile_subtitle_on)
        val subtitleOff = context.getString(R.string.quick_settings_tile_subtitle_off)
        val descActive = context.getString(R.string.quick_settings_tile_desc_active)
        val descInactive = context.getString(R.string.quick_settings_tile_desc_inactive)

        assertEquals("Vanish", label)
        assertEquals("Active", subtitleOn)
        assertEquals("Disabled", subtitleOff)
        assertTrue(descActive.isNotEmpty())
        assertTrue(descInactive.isNotEmpty())
    }

    @Test
    fun settingsRepository_togglesEnabledStateReliably() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val container = AppContainer(context)
        val repo = container.settingsRepository

        // Toggle to OFF
        repo.setEnabled(false)
        val disabledSettings = repo.settingsFlow.first()
        assertFalse(disabledSettings.enabled)

        // Toggle back to ON
        repo.setEnabled(true)
        val enabledSettings = repo.settingsFlow.first()
        assertTrue(enabledSettings.enabled)
    }

    @Test
    fun requestTileUpdate_executesSafely() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Should not crash even in headless/testing environment
        VanishTileService.requestTileUpdate(context)
    }
}
