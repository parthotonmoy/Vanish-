package com.example.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.VeilTheme

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = VeilTheme.colors
    val typography = VeilTheme.typography

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = typography.sectionTitle,
                color = colors.textPrimary
            )
        },
        text = {
            Text(
                text = message,
                style = typography.body,
                color = colors.textSecondary
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                modifier = Modifier.testTag("dialog_confirm_button")
            ) {
                Text(
                    text = confirmLabel,
                    style = typography.button,
                    color = colors.accent
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_cancel_button")
            ) {
                Text(
                    text = stringResource(R.string.action_cancel),
                    style = typography.button,
                    color = colors.textSecondary
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = colors.surfaceElevated,
        modifier = modifier.testTag("confirmation_dialog")
    )
}
