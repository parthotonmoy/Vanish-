package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.VeilTheme

@Composable
fun MasterSwitchRow(
    enabled: Boolean,
    serviceConnected: Boolean,
    statusText: String,
    onToggle: (Boolean) -> Unit,
    onNavigateSetup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = VeilTheme.colors
    val typography = VeilTheme.typography

    val isInteractive = serviceConnected

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .testTag("master_switch_row")
            .clickable(
                enabled = !isInteractive,
                onClick = onNavigateSetup
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.VisibilityOff,
                contentDescription = null,
                tint = if (enabled && isInteractive) colors.accent else colors.textSecondary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = stringResource(R.string.master_switch_title),
                    style = typography.bodyLarge,
                    color = colors.textPrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = statusText,
                    style = typography.body,
                    color = if (!serviceConnected) colors.warning else colors.textSecondary
                )
            }
        }

        Switch(
            checked = enabled && isInteractive,
            onCheckedChange = { onToggle(it) },
            enabled = isInteractive,
            modifier = Modifier.testTag("master_switch"),
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.onAccent,
                checkedTrackColor = colors.accent,
                uncheckedThumbColor = colors.textSecondary,
                uncheckedTrackColor = colors.surfaceElevated,
                disabledCheckedThumbColor = colors.disabledContent,
                disabledCheckedTrackColor = colors.divider
            )
        )
    }
}
