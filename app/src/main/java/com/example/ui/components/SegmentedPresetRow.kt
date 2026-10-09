package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.VeilSettings
import com.example.ui.theme.VeilTheme

@Composable
fun SegmentedPresetRow(
    currentCoverage: Float,
    onSelectPreset: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = VeilTheme.colors
    val typography = VeilTheme.typography

    val isCompact = Math.abs(currentCoverage - VeilSettings.PRESET_COMPACT) < 0.02f
    val isStandard = Math.abs(currentCoverage - VeilSettings.PRESET_STANDARD) < 0.02f
    val isFull = Math.abs(currentCoverage - VeilSettings.PRESET_FULL) < 0.02f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceElevated)
            .border(1.dp, colors.divider, RoundedCornerShape(8.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PresetPill(
            label = stringResource(R.string.preset_compact),
            percentLabel = "40%",
            isSelected = isCompact,
            testTag = "preset_compact",
            onClick = { onSelectPreset(VeilSettings.PRESET_COMPACT) },
            modifier = Modifier.weight(1f)
        )

        PresetPill(
            label = stringResource(R.string.preset_standard),
            percentLabel = "70%",
            isSelected = isStandard,
            testTag = "preset_standard",
            onClick = { onSelectPreset(VeilSettings.PRESET_STANDARD) },
            modifier = Modifier.weight(1f)
        )

        PresetPill(
            label = stringResource(R.string.preset_tall),
            percentLabel = "100%",
            isSelected = isFull,
            testTag = "preset_tall",
            onClick = { onSelectPreset(VeilSettings.PRESET_FULL) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PresetPill(
    label: String,
    percentLabel: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = VeilTheme.colors
    val typography = VeilTheme.typography

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) colors.accent else colors.surfaceElevated)
            .clickable(onClick = onClick)
            .testTag(testTag)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$label ($percentLabel)",
            style = typography.button,
            color = if (isSelected) colors.onAccent else colors.textPrimary
        )
    }
}
