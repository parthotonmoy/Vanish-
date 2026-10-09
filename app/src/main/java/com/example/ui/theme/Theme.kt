package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.example.domain.ThemeMode

val LocalVeilColors = staticCompositionLocalOf { DarkVeilColors }
val LocalVeilTypography = staticCompositionLocalOf { VeilTypography() }

object VeilTheme {
    val colors: VeilColors
        @Composable
        @ReadOnlyComposable
        get() = LocalVeilColors.current

    val typography: VeilTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalVeilTypography.current
}

@Composable
fun VeilTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val context = LocalContext.current
    val baseColors = if (isDark) DarkVeilColors else LightVeilColors

    val finalColors = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val dynamicM3 = if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        baseColors.copy(
            accent = dynamicM3.primary,
            onAccent = dynamicM3.onPrimary
        )
    } else {
        baseColors
    }

    val m3ColorScheme = if (isDark) {
        darkColorScheme(
            primary = finalColors.accent,
            onPrimary = finalColors.onAccent,
            background = finalColors.background,
            surface = finalColors.surface,
            onBackground = finalColors.textPrimary,
            onSurface = finalColors.textPrimary,
            outline = finalColors.divider
        )
    } else {
        lightColorScheme(
            primary = finalColors.accent,
            onPrimary = finalColors.onAccent,
            background = finalColors.background,
            surface = finalColors.surface,
            onBackground = finalColors.textPrimary,
            onSurface = finalColors.textPrimary,
            outline = finalColors.divider
        )
    }

    CompositionLocalProvider(
        LocalVeilColors provides finalColors,
        LocalVeilTypography provides VeilTypography()
    ) {
        MaterialTheme(
            colorScheme = m3ColorScheme,
            content = content
        )
    }
}
