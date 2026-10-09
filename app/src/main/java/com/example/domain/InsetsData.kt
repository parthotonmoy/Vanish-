package com.example.domain

/**
 * Pure JVM data class representing safe area insets (status bar, navigation bar, cutouts).
 */
data class InsetsData(
    val top: Int = 0,
    val bottom: Int = 0,
    val left: Int = 0,
    val right: Int = 0
) {
    companion object {
        val Zero = InsetsData(0, 0, 0, 0)
    }
}
