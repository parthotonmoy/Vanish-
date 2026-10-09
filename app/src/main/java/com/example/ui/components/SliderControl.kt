package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.VeilTheme

@Composable
fun SliderControl(
    title: String,
    valueFraction: Float,
    minFraction: Float,
    maxFraction: Float,
    defaultFraction: Float,
    step: Float,
    helperText: String,
    icon: ImageVector,
    testTagKey: String,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = VeilTheme.colors
    val typography = VeilTheme.typography

    val currentPercent = Math.round(valueFraction * 100f)
    val isDefault = Math.abs(valueFraction - defaultFraction) < 0.01f

    val stepsCount = Math.round((maxFraction - minFraction) / step) - 1

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Top row: Icon + Title (left), Numeric value readout (right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    style = typography.bodyLarge,
                    color = colors.textPrimary
                )
            }

            Text(
                text = "$currentPercent%",
                style = typography.numeric,
                color = colors.accent
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Slider track
        Slider(
            value = valueFraction,
            onValueChange = onValueChange,
            valueRange = minFraction..maxFraction,
            steps = stepsCount.coerceAtLeast(0),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("slider_$testTagKey"),
            colors = SliderDefaults.colors(
                thumbColor = colors.accent,
                activeTrackColor = colors.accent,
                inactiveTrackColor = colors.divider
            )
        )

        // Bottom row: Min/Max labels and Reset button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${Math.round(minFraction * 100f)}%",
                style = typography.label,
                color = colors.textSecondary
            )

            TextButton(
                onClick = { onValueChange(defaultFraction) },
                enabled = !isDefault,
                modifier = Modifier.testTag("reset_$testTagKey")
            ) {
                Text(
                    text = stringResource(R.string.action_reset),
                    style = typography.button,
                    color = if (!isDefault) colors.accent else colors.disabledContent
                )
            }

            Text(
                text = "${Math.round(maxFraction * 100f)}%",
                style = typography.label,
                color = colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = helperText,
            style = typography.body,
            color = colors.textSecondary
        )
    }
}
