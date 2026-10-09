package com.example.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.ui.theme.VeilTheme

enum class VeilStatusState {
    ACTIVE,
    READY,
    NEEDS_SETUP,
    DISCONNECTED,
    OFF
}

@Composable
fun StatusIndicator(
    state: VeilStatusState,
    label: String,
    modifier: Modifier = Modifier
) {
    val colors = VeilTheme.colors
    val typography = VeilTheme.typography

    val (icon: ImageVector, tint) = when (state) {
        VeilStatusState.ACTIVE -> Pair(Icons.Outlined.CheckCircle, colors.success)
        VeilStatusState.READY -> Pair(Icons.Outlined.CheckCircle, colors.success)
        VeilStatusState.NEEDS_SETUP -> Pair(Icons.Outlined.WarningAmber, colors.warning)
        VeilStatusState.DISCONNECTED -> Pair(Icons.Outlined.ErrorOutline, colors.error)
        VeilStatusState.OFF -> Pair(Icons.Outlined.PauseCircle, colors.textSecondary)
    }

    Row(
        modifier = modifier.semantics { this.liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = label,
            style = typography.body,
            color = colors.textPrimary
        )
    }
}
