package com.example.ui.screens

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon as AndroidIcon
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatterySaver
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Height
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Opacity
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.example.domain.VeilSettings
import com.example.platform.VanishTileService
import com.example.ui.components.DeveloperSection
import com.example.ui.components.LivePreview
import com.example.ui.components.MasterSwitchRow
import com.example.ui.components.SegmentedPresetRow
import com.example.ui.components.SliderControl
import com.example.ui.theme.VeilTheme
import com.example.viewmodel.MainUiState

@Composable
fun MainScreen(
    uiState: MainUiState,
    onToggleMaster: (Boolean) -> Unit,
    onOpacityChange: (Float) -> Unit,
    onCoverageChange: (Float) -> Unit,
    onOverlapChange: (Float) -> Unit,
    onNavigateSetup: () -> Unit,
    onNavigateTroubleshooting: () -> Unit,
    onNavigateSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = VeilTheme.colors
    val typography = VeilTheme.typography
    val context = LocalContext.current

    val statusText = when {
        !uiState.isAccessibilityEnabled || !uiState.isServiceConnected ->
            stringResource(R.string.status_needs_setup)
        !uiState.settings.enabled ->
            stringResource(R.string.status_off)
        else ->
            stringResource(R.string.status_ready)
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = colors.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = typography.screenTitle,
                    color = colors.textPrimary
                )

                IconButton(
                    onClick = onNavigateSettings,
                    modifier = Modifier.testTag("action_settings")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = stringResource(R.string.settings_title),
                        tint = colors.textSecondary
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            val isWide = maxWidth >= 600.dp

            if (isWide) {
                // Two-Pane Adaptive Layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Left Pane: Master switch, Setup Banner, Live Preview
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 16.dp)
                    ) {
                        MasterSwitchRow(
                            enabled = uiState.settings.enabled,
                            serviceConnected = uiState.isServiceConnected,
                            statusText = statusText,
                            onToggle = onToggleMaster,
                            onNavigateSetup = onNavigateSetup
                        )

                        if (uiState.needsSetup) {
                            Spacer(modifier = Modifier.height(12.dp))
                            SetupBanner(onNavigateSetup = onNavigateSetup)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        LivePreview(settings = uiState.settings)
                    }

                    // Right Pane: Sliders and Informational Sections
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 16.dp)
                    ) {
                        ControlsSection(
                            settings = uiState.settings,
                            onOpacityChange = onOpacityChange,
                            onCoverageChange = onCoverageChange,
                            onOverlapChange = onOverlapChange
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        InfoSections(
                            onNavigateTroubleshooting = onNavigateTroubleshooting,
                            onOpenAppInfo = { openAppInfo(context) }
                        )

                        DeveloperSection()
                    }
                }
            } else {
                // Single Column Handheld Layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 640.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    MasterSwitchRow(
                        enabled = uiState.settings.enabled,
                        serviceConnected = uiState.isServiceConnected,
                        statusText = statusText,
                        onToggle = onToggleMaster,
                        onNavigateSetup = onNavigateSetup
                    )

                    if (uiState.needsSetup) {
                        Spacer(modifier = Modifier.height(12.dp))
                        SetupBanner(onNavigateSetup = onNavigateSetup)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    LivePreview(settings = uiState.settings)

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = colors.divider, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    ControlsSection(
                        settings = uiState.settings,
                        onOpacityChange = onOpacityChange,
                        onCoverageChange = onCoverageChange,
                        onOverlapChange = onOverlapChange
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = colors.divider, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    InfoSections(
                        onNavigateTroubleshooting = onNavigateTroubleshooting,
                        onOpenAppInfo = { openAppInfo(context) }
                    )

                    DeveloperSection()
                }
            }
        }
    }
}

@Composable
private fun SetupBanner(
    onNavigateSetup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = VeilTheme.colors
    val typography = VeilTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .border(1.dp, colors.warning, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.WarningAmber,
                contentDescription = null,
                tint = colors.warning,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = stringResource(R.string.setup_banner_message),
                style = typography.body,
                color = colors.textPrimary
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onNavigateSetup,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.accent,
                contentColor = colors.onAccent
            ),
            modifier = Modifier.testTag("setup_banner_action")
        ) {
            Text(
                text = stringResource(R.string.setup_banner_action),
                style = typography.button
            )
        }
    }
}

@Composable
private fun ControlsSection(
    settings: VeilSettings,
    onOpacityChange: (Float) -> Unit,
    onCoverageChange: (Float) -> Unit,
    onOverlapChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // 1. Opacity control
        SliderControl(
            title = stringResource(R.string.control_opacity_title),
            valueFraction = settings.opacity,
            minFraction = VeilSettings.MIN_OPACITY,
            maxFraction = VeilSettings.MAX_OPACITY,
            defaultFraction = VeilSettings.DEFAULT_OPACITY,
            step = 0.05f,
            helperText = stringResource(R.string.control_opacity_helper),
            icon = Icons.Outlined.Opacity,
            testTagKey = "opacity",
            onValueChange = onOpacityChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Coverage control
        SliderControl(
            title = stringResource(R.string.control_coverage_title),
            valueFraction = settings.coverage,
            minFraction = VeilSettings.MIN_COVERAGE,
            maxFraction = VeilSettings.MAX_COVERAGE,
            defaultFraction = VeilSettings.DEFAULT_COVERAGE,
            step = 0.05f,
            helperText = stringResource(R.string.control_coverage_helper),
            icon = Icons.Outlined.Height,
            testTagKey = "coverage",
            onValueChange = onCoverageChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Presets for coverage
        SegmentedPresetRow(
            currentCoverage = settings.coverage,
            onSelectPreset = onCoverageChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Position (Keyboard Clearance)
        if (VeilSettings.MAX_OVERLAP > 0f) {
            SliderControl(
                title = stringResource(R.string.control_overlap_title),
                valueFraction = settings.overlap,
                minFraction = VeilSettings.MIN_OVERLAP,
                maxFraction = VeilSettings.MAX_OVERLAP,
                defaultFraction = VeilSettings.DEFAULT_OVERLAP,
                step = 0.05f,
                helperText = stringResource(R.string.control_overlap_helper),
                icon = Icons.Outlined.SwapVert,
                testTagKey = "overlap",
                onValueChange = onOverlapChange
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(VeilTheme.colors.surfaceElevated)
                    .border(1.dp, VeilTheme.colors.divider, RoundedCornerShape(10.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.SwapVert,
                    contentDescription = null,
                    tint = VeilTheme.colors.accent,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = stringResource(R.string.control_overlap_title),
                        style = VeilTheme.typography.bodyLarge,
                        color = VeilTheme.colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Vanish covers the screen area above the keyboard. Key area and keyboard dimensions remain completely uncovered.",
                        style = VeilTheme.typography.body,
                        color = VeilTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoSections(
    onNavigateTroubleshooting: () -> Unit,
    onOpenAppInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = VeilTheme.colors
    val typography = VeilTheme.typography

    Column(modifier = modifier.fillMaxWidth()) {
        // Automatic activation info
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Outlined.Keyboard,
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = stringResource(R.string.section_automatic_activation),
                    style = typography.sectionTitle,
                    color = colors.textPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.automatic_activation_desc),
                    style = typography.body,
                    color = colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Settings Tile
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Outlined.Tune,
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = stringResource(R.string.quick_settings_section_title),
                    style = typography.sectionTitle,
                    color = colors.textPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.quick_settings_section_desc),
                    style = typography.body,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.quick_settings_guide_steps),
                    style = typography.label,
                    color = colors.textSecondary
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            val sbm = context.getSystemService(StatusBarManager::class.java)
                            sbm?.requestAddTileService(
                                ComponentName(context, VanishTileService::class.java),
                                context.getString(R.string.quick_settings_tile_label),
                                AndroidIcon.createWithResource(context, R.drawable.ic_quick_settings_tile),
                                context.mainExecutor,
                                java.util.function.Consumer<Int> { /* result */ }
                            )
                        },
                        modifier = Modifier.testTag("action_add_qs_tile")
                    ) {
                        Text(
                            text = stringResource(R.string.quick_settings_add_prompt_button),
                            style = typography.button,
                            color = colors.accent
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Permissions & Compatibility
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Outlined.Build,
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = stringResource(R.string.section_permissions),
                    style = typography.sectionTitle,
                    color = colors.textPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.permissions_desc),
                    style = typography.body,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onNavigateTroubleshooting,
                    modifier = Modifier.testTag("action_troubleshooting")
                ) {
                    Text(
                        text = stringResource(R.string.action_troubleshooting),
                        style = typography.button,
                        color = colors.accent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Battery & Reliability
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Outlined.BatterySaver,
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = stringResource(R.string.section_battery),
                    style = typography.sectionTitle,
                    color = colors.textPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.battery_desc),
                    style = typography.body,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onOpenAppInfo,
                    modifier = Modifier.testTag("action_app_info")
                ) {
                    Text(
                        text = stringResource(R.string.action_app_info),
                        style = typography.button,
                        color = colors.accent
                    )
                }
            }
        }
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
        // Handled gracefully
    }
}
