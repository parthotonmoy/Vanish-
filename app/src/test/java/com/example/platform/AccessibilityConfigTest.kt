package com.example.platform

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AccessibilityConfigTest {

    @Test
    fun accessibilityConfigFile_containsOnlyRequiredAttributes() {
        val candidates = listOf(
            File("src/main/res/xml/accessibility_service_config.xml"),
            File("app/src/main/res/xml/accessibility_service_config.xml")
        )
        val file = candidates.firstOrNull { it.exists() }
            ?: throw IllegalStateException("accessibility_service_config.xml not found in ${candidates.map { it.absolutePath }}")

        val content = file.readText()

        // Must declare typeWindowsChanged
        assertTrue("Must include typeWindowsChanged", content.contains("typeWindowsChanged"))

        // Must declare flagRetrieveInteractiveWindows
        assertTrue("Must include flagRetrieveInteractiveWindows", content.contains("flagRetrieveInteractiveWindows"))

        // Must NOT include key event filtering or gestures
        assertFalse("Must not filter key events", content.contains("canRequestFilterKeyEvents"))
        assertFalse("Must not request gestures", content.contains("canPerformGestures"))
        assertFalse("Must not request screenshot", content.contains("canTakeScreenshot"))
        assertFalse("Must not request touch exploration", content.contains("canRequestTouchExplorationMode"))
    }
}
