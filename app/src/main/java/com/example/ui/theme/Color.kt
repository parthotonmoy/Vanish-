package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class VeilColors(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val accent: Color,
    val accentSecondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val divider: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val onAccent: Color,
    val disabledContent: Color,
    val veilTint: Color,
    val scrim: Color,
    val previewKeyBackgroundDark: Color,
    val previewKeyBackgroundLight: Color,
    val previewKeyOutlineDark: Color,
    val previewKeyOutlineLight: Color,
    val previewFieldBgDark: Color,
    val previewFieldBgLight: Color
)

val DarkVeilColors = VeilColors(
    background = Color(0xFF0C1015),
    surface = Color(0xFF141B23),
    surfaceElevated = Color(0xFF1B2530),
    accent = Color(0xFF8CB7FF),
    accentSecondary = Color(0xFF7ADBC8),
    textPrimary = Color(0xFFF0F4F8),
    textSecondary = Color(0xFFA7B2C0),
    divider = Color(0xFF2A3542),
    success = Color(0xFF7ED6A5),
    warning = Color(0xFFF1C879),
    error = Color(0xFFFF8C8C),
    onAccent = Color(0xFF0C1015),
    disabledContent = Color(0xFFA7B2C0).copy(alpha = 0.38f),
    veilTint = Color(0xFF000000),
    scrim = Color(0x99000000),
    previewKeyBackgroundDark = Color(0xFF1E2630),
    previewKeyBackgroundLight = Color(0xFFE2E7ED),
    previewKeyOutlineDark = Color(0xFF2C3847),
    previewKeyOutlineLight = Color(0xFFCBD3DC),
    previewFieldBgDark = Color(0xFF182029),
    previewFieldBgLight = Color(0xFFECEFF3)
)

val LightVeilColors = VeilColors(
    background = Color(0xFFF5F7FA),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFEDF1F6),
    accent = Color(0xFF285DB8),
    accentSecondary = Color(0xFF147D70),
    textPrimary = Color(0xFF17212B),
    textSecondary = Color(0xFF586777),
    divider = Color(0xFFDCE3EB),
    success = Color(0xFF217A4B),
    warning = Color(0xFF8C5C0A),
    error = Color(0xFFB3261E),
    onAccent = Color(0xFFFFFFFF),
    disabledContent = Color(0xFF586777).copy(alpha = 0.38f),
    veilTint = Color(0xFF000000),
    scrim = Color(0x99000000),
    previewKeyBackgroundDark = Color(0xFF1E2630),
    previewKeyBackgroundLight = Color(0xFFE2E7ED),
    previewKeyOutlineDark = Color(0xFF2C3847),
    previewKeyOutlineLight = Color(0xFFCBD3DC),
    previewFieldBgDark = Color(0xFF182029),
    previewFieldBgLight = Color(0xFFECEFF3)
)
