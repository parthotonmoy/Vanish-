package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.BandGeometry
import com.example.domain.InsetsData
import com.example.domain.IntRect
import com.example.domain.VeilSettings
import com.example.ui.theme.VeilTheme

@Composable
fun LivePreview(
    settings: VeilSettings,
    modifier: Modifier = Modifier
) {
    val colors = VeilTheme.colors
    val typography = VeilTheme.typography

    var isDarkKeys by remember { mutableStateOf(false) }

    val description = "Live preview: Opacity ${settings.opacityPercentage}%, " +
            "Coverage ${settings.coveragePercentage}%, Overlap ${settings.overlapPercentage}%"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_preview_container")
            .semantics { this.contentDescription = description }
    ) {
        // Toggle for Light keys / Dark keys silhouette
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.preview_title),
                style = typography.sectionTitle,
                color = colors.textPrimary
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surfaceElevated)
                    .border(1.dp, colors.divider, RoundedCornerShape(8.dp))
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (!isDarkKeys) colors.accent else colors.surfaceElevated)
                        .clickable { isDarkKeys = false }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.preview_light_keys),
                        style = typography.label,
                        color = if (!isDarkKeys) colors.onAccent else colors.textSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isDarkKeys) colors.accent else colors.surfaceElevated)
                        .clickable { isDarkKeys = true }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.preview_dark_keys),
                        style = typography.label,
                        color = if (isDarkKeys) colors.onAccent else colors.textSecondary
                    )
                }
            }
        }

        // Preview Well
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.surfaceElevated)
                .border(1.dp, colors.divider, RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.divider, RoundedCornerShape(8.dp))
            ) {
                val canvasWidth = constraints.maxWidth
                val canvasHeight = constraints.maxHeight

                val imeHeight = (canvasHeight * 0.38f).toInt()
                val imeTop = canvasHeight - imeHeight

                val syntheticDisplay = IntRect(0, 0, canvasWidth, canvasHeight)
                val syntheticInsets = InsetsData(top = 16, bottom = 0, left = 0, right = 0)
                val syntheticIme = IntRect(0, imeTop, canvasWidth, canvasHeight)

                val band = BandGeometry.computeBand(
                    displayBounds = syntheticDisplay,
                    safeInsets = syntheticInsets,
                    imeBounds = syntheticIme,
                    coverageFraction = settings.coverage,
                    overlapFraction = settings.overlap
                )

                val targetAlpha = if (settings.enabled && band != null) settings.opacity else 0f
                val animatedAlpha by androidx.compose.animation.core.animateFloatAsState(
                    targetValue = targetAlpha,
                    animationSpec = androidx.compose.animation.core.tween(
                        durationMillis = 150,
                        easing = androidx.compose.animation.core.FastOutSlowInEasing
                    ),
                    label = "vanish_preview_alpha"
                )

                val keyBackground = if (isDarkKeys) colors.previewKeyBackgroundDark else colors.previewKeyBackgroundLight
                val keyOutline = if (isDarkKeys) colors.previewKeyOutlineDark else colors.previewKeyOutlineLight
                val fieldBg = if (isDarkKeys) colors.previewFieldBgDark else colors.previewFieldBgLight

                Canvas(modifier = Modifier.fillMaxSize()) {
                    // 1. Mock app text field where typing occurs
                    val fieldTop = (imeTop - 48.dp.toPx()).coerceAtLeast(16.dp.toPx())
                    drawRoundRect(
                        color = fieldBg,
                        topLeft = Offset(16.dp.toPx(), fieldTop),
                        size = Size(size.width - 32.dp.toPx(), 36.dp.toPx()),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )

                    // Mock text line
                    drawRect(
                        color = colors.textSecondary.copy(alpha = 0.5f),
                        topLeft = Offset(24.dp.toPx(), fieldTop + 14.dp.toPx()),
                        size = Size(size.width * 0.45f, 8.dp.toPx())
                    )

                    // 2. Keyboard silhouette (lower 38%)
                    drawRect(
                        color = keyBackground,
                        topLeft = Offset(0f, imeTop.toFloat()),
                        size = Size(size.width, imeHeight.toFloat())
                    )

                    // Keyboard header divider
                    drawLine(
                        color = keyOutline,
                        start = Offset(0f, imeTop.toFloat()),
                        end = Offset(size.width, imeTop.toFloat()),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Simulated key rows
                    val rowHeight = (imeHeight - 12) / 3f
                    for (row in 0..2) {
                        val rowTop = imeTop + 6 + (row * rowHeight)
                        val keyCount = if (row == 2) 7 else 9
                        val keyWidth = (size.width - 16) / keyCount
                        for (k in 0 until keyCount) {
                            drawRoundRect(
                                color = keyOutline,
                                topLeft = Offset(8 + (k * keyWidth) + 1, rowTop),
                                size = Size(keyWidth - 2, rowHeight - 4),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx(), 3.dp.toPx())
                            )
                        }
                    }

                    // 3. Privacy Band Overlay with smooth fade
                    if (band != null && animatedAlpha > 0.001f) {
                        val bandTop = band.top.toFloat()
                        val bandH = band.height.toFloat()
                        val bandW = size.width

                        // Filled veil tint with smoothly animated opacity
                        drawRect(
                            color = colors.veilTint.copy(alpha = animatedAlpha),
                            topLeft = Offset(0f, bandTop),
                            size = Size(bandW, bandH)
                        )

                        // Outline in accentSecondary to delineate covered region
                        val strokeAlpha = (animatedAlpha / settings.opacity.coerceAtLeast(0.01f)).coerceIn(0f, 1f)
                        drawRect(
                            color = colors.accentSecondary.copy(alpha = strokeAlpha),
                            topLeft = Offset(0f, bandTop),
                            size = Size(bandW, bandH),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.preview_caption),
            style = typography.label,
            color = colors.textSecondary
        )
    }
}
