package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.ThemeMode
import com.example.ui.components.ConfirmationDialog
import com.example.ui.theme.VeilTheme
import com.example.viewmodel.SettingsUiState

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onDynamicColorToggled: (Boolean) -> Unit,
    onShowResetDialog: (Boolean) -> Unit,
    onConfirmReset: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = VeilTheme.colors
    val typography = VeilTheme.typography

    BackHandler { onNavigateBack() }

    if (uiState.showResetDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.dialog_reset_title),
            message = stringResource(R.string.dialog_reset_message),
            confirmLabel = stringResource(R.string.action_reset),
            onConfirm = onConfirmReset,
            onDismiss = { onShowResetDialog(false) }
        )
    }

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
                    modifier = Modifier.testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.action_cancel),
                        tint = colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.settings_title),
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
            // Theme Section
            Text(
                text = stringResource(R.string.settings_theme_title),
                style = typography.sectionTitle,
                color = colors.textPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            ThemeSelector(
                selectedMode = uiState.settings.themeMode,
                onSelectMode = onThemeModeSelected
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = colors.divider, thickness = 1.dp)
            Spacer(modifier = Modifier.height(20.dp))

            // Dynamic Color Toggle (API 31+ only)
            if (uiState.supportsDynamicColor) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ColorLens,
                            contentDescription = null,
                            tint = colors.textSecondary
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = stringResource(R.string.settings_dynamic_color_title),
                                style = typography.bodyLarge,
                                color = colors.textPrimary
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = stringResource(R.string.settings_dynamic_color_desc),
                                style = typography.body,
                                color = colors.textSecondary
                            )
                        }
                    }

                    Switch(
                        checked = uiState.settings.dynamicColor,
                        onCheckedChange = onDynamicColorToggled,
                        modifier = Modifier.testTag("switch_dynamic_color"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.onAccent,
                            checkedTrackColor = colors.accent,
                            uncheckedThumbColor = colors.textSecondary,
                            uncheckedTrackColor = colors.surfaceElevated
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = colors.divider, thickness = 1.dp)
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Reset Appearance
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onShowResetDialog(true) }
                    .padding(vertical = 8.dp)
                    .testTag("row_reset_appearance"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.RestartAlt,
                    contentDescription = null,
                    tint = colors.accent
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = stringResource(R.string.settings_reset_appearance_title),
                        style = typography.bodyLarge,
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = stringResource(R.string.settings_reset_appearance_desc),
                        style = typography.body,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = colors.divider, thickness = 1.dp)
            Spacer(modifier = Modifier.height(20.dp))

            // App Version
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = colors.textSecondary
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = stringResource(R.string.app_version_title),
                        style = typography.bodyLarge,
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = stringResource(R.string.app_version_value),
                        style = typography.body,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeSelector(
    selectedMode: ThemeMode,
    onSelectMode: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = VeilTheme.colors
    val typography = VeilTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceElevated)
            .border(1.dp, colors.divider, RoundedCornerShape(8.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ThemePill(
            label = stringResource(R.string.theme_system),
            isSelected = selectedMode == ThemeMode.SYSTEM,
            onClick = { onSelectMode(ThemeMode.SYSTEM) },
            testTag = "theme_system",
            modifier = Modifier.weight(1f)
        )

        ThemePill(
            label = stringResource(R.string.theme_light),
            isSelected = selectedMode == ThemeMode.LIGHT,
            onClick = { onSelectMode(ThemeMode.LIGHT) },
            testTag = "theme_light",
            modifier = Modifier.weight(1f)
        )

        ThemePill(
            label = stringResource(R.string.theme_dark),
            isSelected = selectedMode == ThemeMode.DARK,
            onClick = { onSelectMode(ThemeMode.DARK) },
            testTag = "theme_dark",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ThemePill(
    label: String,
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
            text = label,
            style = typography.button,
            color = if (isSelected) colors.onAccent else colors.textPrimary
        )
    }
}
