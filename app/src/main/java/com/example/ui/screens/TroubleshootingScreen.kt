package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.CheckResult
import com.example.ui.components.TroubleshootingItem
import com.example.ui.theme.VeilTheme
import com.example.viewmodel.TroubleshootingUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TroubleshootingScreen(
    uiState: TroubleshootingUiState,
    onTestInputChanged: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = VeilTheme.colors
    val typography = VeilTheme.typography

    BackHandler { onNavigateBack() }

    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Detect actual IME insets to bring focused field into view
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    val isImeVisible = imeBottom > 0

    LaunchedEffect(isImeVisible) {
        if (isImeVisible) {
            delay(50)
            bringIntoViewRequester.bringIntoView()
        }
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
                    modifier = Modifier.testTag("troubleshooting_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.action_cancel),
                        tint = colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.troubleshooting_title),
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
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Interactive Test Field Section
            Text(
                text = stringResource(R.string.test_field_title),
                style = typography.sectionTitle,
                color = colors.textPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.test_field_note),
                style = typography.body,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.testInputText,
                onValueChange = onTestInputChanged,
                placeholder = {
                    Text(
                        text = stringResource(R.string.test_field_hint),
                        style = typography.body,
                        color = colors.textSecondary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("interactive_test_field")
                    .bringIntoViewRequester(bringIntoViewRequester)
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            coroutineScope.launch {
                                delay(100)
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accent,
                    unfocusedBorderColor = colors.divider,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // System Self-Check Section
            Text(
                text = stringResource(R.string.self_check_title),
                style = typography.sectionTitle,
                color = colors.textPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Check 1: Accessibility Enabled
            TroubleshootingItem(
                title = stringResource(R.string.check_accessibility_enabled),
                description = if (uiState.isAccessibilityEnabled)
                    "Service is turned on in Android system settings."
                else
                    "Service toggle is off. Turn on in Accessibility settings.",
                result = if (uiState.isAccessibilityEnabled) CheckResult.PASS else CheckResult.WARN
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Check 2: Service Process Connected
            TroubleshootingItem(
                title = stringResource(R.string.check_service_connected),
                description = if (uiState.isServiceConnected)
                    "Android system has bound to Vanish accessibility service."
                else
                    "Service process not currently bound by OS.",
                result = if (uiState.isServiceConnected) CheckResult.PASS else CheckResult.FAIL
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Check 3: Overlay Ready
            TroubleshootingItem(
                title = stringResource(R.string.check_overlay_ready),
                description = if (uiState.isServiceConnected)
                    "OverlayController initialized and ready for keyboard events."
                else
                    "Waiting for service connection to initialize overlay.",
                result = if (uiState.isServiceConnected) CheckResult.PASS else CheckResult.IDLE
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Known Limitations Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surfaceElevated)
                    .border(1.dp, colors.divider, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.limitations_title),
                    style = typography.sectionTitle,
                    color = colors.textPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.limitations_desc),
                    style = typography.body,
                    color = colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
