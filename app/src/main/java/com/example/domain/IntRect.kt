package com.example.domain

/**
 * Pure JVM integer rectangle representing screen or window coordinates.
 * Independent of Android framework classes for zero-dependency JVM unit testing.
 */
data class IntRect(
    val left: Int = 0,
    val top: Int = 0,
    val right: Int = 0,
    val bottom: Int = 0
) {
    val width: Int get() = (right - left).coerceAtLeast(0)
    val height: Int get() = (bottom - top).coerceAtLeast(0)
    val isEmpty: Boolean get() = width <= 0 || height <= 0

    companion object {
        val Empty = IntRect(0, 0, 0, 0)
    }
}
