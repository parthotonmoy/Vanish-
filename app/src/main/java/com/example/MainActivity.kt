package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.navigation.Screen
import com.example.ui.screens.MainScreen
import com.example.ui.screens.SetupScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TroubleshootingScreen
import com.example.ui.theme.VeilTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.SettingsViewModel
import com.example.viewmodel.SetupViewModel
import com.example.viewmodel.TroubleshootingViewModel

class MainActivity : ComponentActivity() {

    private val container by lazy {
        (application as VeilApplication).container
    }

    private val mainViewModel by viewModels<MainViewModel> {
        viewModelFactory { MainViewModel(container) }
    }

    private val setupViewModel by viewModels<SetupViewModel> {
        viewModelFactory { SetupViewModel(container) }
    }

    private val troubleshootingViewModel by viewModels<TroubleshootingViewModel> {
        viewModelFactory { TroubleshootingViewModel(container) }
    }

    private val settingsViewModel by viewModels<SettingsViewModel> {
        viewModelFactory { SettingsViewModel(container) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()
            val setupUiState by setupViewModel.uiState.collectAsStateWithLifecycle()
            val troubleshootingUiState by troubleshootingViewModel.uiState.collectAsStateWithLifecycle()
            val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

            var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }

            // Automatically prompt setup if needed and not previously dismissed
            LaunchedEffect(mainUiState.needsSetup, mainUiState.settings.setupDismissed) {
                if (mainUiState.needsSetup && !mainUiState.settings.setupDismissed && currentScreen == Screen.Main) {
                    currentScreen = Screen.Setup
                }
            }

            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                mainViewModel.refreshStatus()
                setupViewModel.refreshStatus()
                troubleshootingViewModel.refreshStatus()
            }

            VeilTheme(
                themeMode = mainUiState.settings.themeMode,
                dynamicColor = mainUiState.settings.dynamicColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = VeilTheme.colors.background
                ) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            Screen.Main -> MainScreen(
                                uiState = mainUiState,
                                onToggleMaster = mainViewModel::setMasterEnabled,
                                onOpacityChange = mainViewModel::setOpacity,
                                onCoverageChange = mainViewModel::setCoverage,
                                onOverlapChange = mainViewModel::setOverlap,
                                onNavigateSetup = { currentScreen = Screen.Setup },
                                onNavigateTroubleshooting = { currentScreen = Screen.Troubleshooting },
                                onNavigateSettings = { currentScreen = Screen.Settings }
                            )

                            Screen.Setup -> SetupScreen(
                                uiState = setupUiState,
                                onNavigateBack = { currentScreen = Screen.Main },
                                onDismissSetup = {
                                    setupViewModel.dismissSetup()
                                    currentScreen = Screen.Main
                                }
                            )

                            Screen.Troubleshooting -> TroubleshootingScreen(
                                uiState = troubleshootingUiState,
                                onTestInputChanged = troubleshootingViewModel::updateTestInput,
                                onNavigateBack = { currentScreen = Screen.Main }
                            )

                            Screen.Settings -> SettingsScreen(
                                uiState = settingsUiState,
                                onThemeModeSelected = settingsViewModel::setThemeMode,
                                onDynamicColorToggled = settingsViewModel::setDynamicColor,
                                onShowResetDialog = settingsViewModel::showResetDialog,
                                onConfirmReset = settingsViewModel::resetAppearance,
                                onNavigateBack = { currentScreen = Screen.Main }
                            )
                        }
                    }
                }
            }
        }
    }

    private inline fun <reified T : ViewModel> viewModelFactory(crossinline creator: () -> T): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
                return creator() as VM
            }
        }
    }
}
