package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.ThemeMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun appName_isVanish() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Vanish", appName)
    }

    @Test
    fun appContainer_initializesSettingsRepository() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val container = AppContainer(context)
        assertNotNull(container.settingsRepository)

        val settings = container.settingsRepository.settingsFlow.first()
        assertEquals(0.55f, settings.opacity, 0.001f)
        assertEquals(1.00f, settings.coverage, 0.001f)
        assertEquals(0.00f, settings.overlap, 0.001f)
        assertEquals(ThemeMode.SYSTEM, settings.themeMode)
    }
}
