package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.SettingsAccessibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.VeilTheme
import com.example.viewmodel.SetupUiState

@Composable
fun SetupScreen(
    uiState: SetupUiState,
    onNavigateBack: () -> Unit,
    onDismissSetup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = VeilTheme.colors
    val typography = VeilTheme.typography
    val context = LocalContext.current

    BackHandler { onNavigateBack() }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = colors.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("setup_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.action_cancel),
                        tint = colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.setup_screen_title),
                    style = typography.screenTitle,
                    color = colors.textPrimary
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // STEP 1: Privacy Disclosure
            SetupStepCard(
                stepNumber = "1",
                icon = Icons.Outlined.Security,
                title = stringResource(R.string.setup_step1_title),
                description = stringResource(R.string.setup_step1_desc)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // STEP 2: Enable in Settings
            SetupStepCard(
                stepNumber = "2",
                icon = Icons.Outlined.SettingsAccessibility,
                title = stringResource(R.string.setup_step2_title),
                description = stringResource(R.string.setup_step2_desc)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { openAccessibilitySettings(context) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accent,
                        contentColor = colors.onAccent
                    ),
                    modifier = Modifier.testTag("action_open_accessibility")
                ) {
                    Text(
                        text = stringResource(R.string.setup_action_open_accessibility),
                        style = typography.button
                    )
                }

                if (uiState.mayRequireRestrictedSettingsNotice && !uiState.isAccessibilityEnabled) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.surfaceElevated)
                            .border(1.dp, colors.warning, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.setup_step2_restricted_note),
                            style = typography.technical,
                            color = colors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { openAppInfo(context) },
                            modifier = Modifier.testTag("action_open_app_info")
                        ) {
                            Text(
                                text = stringResource(R.string.setup_action_open_app_info),
                                style = typography.button,
                                color = colors.accent
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // STEP 3: Status Confirmation
            SetupStepCard(
                stepNumber = "3",
                icon = if (uiState.isServiceConnected) Icons.Outlined.CheckCircle else Icons.Outlined.HourglassEmpty,
                title = stringResource(R.string.setup_step3_title),
                description = if (uiState.isServiceConnected)
                    stringResource(R.string.setup_step3_connected)
                else
                    stringResource(R.string.setup_step3_waiting)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Bottom Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        onDismissSetup()
                        onNavigateBack()
                    },
                    modifier = Modifier.testTag("setup_action_not_now")
                ) {
                    Text(
                        text = stringResource(R.string.setup_action_not_now),
                        style = typography.button,
                        color = colors.textSecondary
                    )
                }

                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accent,
                        contentColor = colors.onAccent
                    ),
                    modifier = Modifier.testTag("setup_action_continue")
                ) {
                    Text(
                        text = stringResource(R.string.setup_action_continue),
                        style = typography.button
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SetupStepCard(
    stepNumber: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)? = null
) {
    val colors = VeilTheme.colors
    val typography = VeilTheme.typography

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .border(1.dp, colors.divider, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                style = typography.sectionTitle,
                color = colors.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            style = typography.body,
            color = colors.textSecondary
        )

        content?.invoke()
    }
}

private fun openAccessibilitySettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        // Handled
    }
}

private fun openAppInfo(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        // Handled
    }
}
