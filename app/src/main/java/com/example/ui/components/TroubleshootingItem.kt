package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.VeilTheme

enum class CheckResult {
    PASS,
    WARN,
    FAIL,
    IDLE
}

@Composable
fun TroubleshootingItem(
    title: String,
    description: String,
    result: CheckResult,
    modifier: Modifier = Modifier
) {
    val colors = VeilTheme.colors
    val typography = VeilTheme.typography

    val (icon: ImageVector, tint, statusText) = when (result) {
        CheckResult.PASS -> Triple(Icons.Outlined.CheckCircle, colors.success, stringResource(R.string.status_pass))
        CheckResult.WARN -> Triple(Icons.Outlined.WarningAmber, colors.warning, stringResource(R.string.status_warn))
        CheckResult.FAIL -> Triple(Icons.Outlined.ErrorOutline, colors.error, stringResource(R.string.status_fail))
        CheckResult.IDLE -> Triple(Icons.Outlined.HelpOutline, colors.textSecondary, stringResource(R.string.status_idle))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceElevated)
            .border(1.dp, colors.divider, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    style = typography.bodyLarge,
                    color = colors.textPrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = description,
                    style = typography.body,
                    color = colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, tint, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = statusText,
                style = typography.label,
                color = tint
            )
        }
    }
}
