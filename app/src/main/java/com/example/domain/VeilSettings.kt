package com.example.domain

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class VeilSettings(
    val enabled: Boolean = true,
    val opacity: Float = DEFAULT_OPACITY,
    val coverage: Float = DEFAULT_COVERAGE,
    val overlap: Float = DEFAULT_OVERLAP,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = false,
    val setupDismissed: Boolean = false
) {
    fun validated(): VeilSettings {
        return copy(
            opacity = opacity.coerceIn(MIN_OPACITY, MAX_OPACITY),
            coverage = coverage.coerceIn(MIN_COVERAGE, MAX_COVERAGE),
            overlap = overlap.coerceIn(MIN_OVERLAP, MAX_OVERLAP)
        )
    }

    val opacityPercentage: Int
        get() = Math.round(opacity * 100f)

    val coveragePercentage: Int
        get() = Math.round(coverage * 100f)

    val overlapPercentage: Int
        get() = Math.round(overlap * 100f)

    companion object {
        const val MIN_OPACITY = 0.10f
        const val MAX_OPACITY = 0.80f
        const val DEFAULT_OPACITY = 0.55f

        const val MIN_COVERAGE = 0.20f
        const val MAX_COVERAGE = 1.00f
        const val DEFAULT_COVERAGE = 1.00f

        const val MIN_OVERLAP = 0.00f
        const val MAX_OVERLAP = 0.00f
        const val DEFAULT_OVERLAP = 0.00f

        const val PRESET_COMPACT = 0.40f
        const val PRESET_STANDARD = 0.70f
        const val PRESET_FULL = 1.00f
    }
}
