package com.example.ui.navigation

sealed interface Screen {
    data object Main : Screen
    data object Setup : Screen
    data object Troubleshooting : Screen
    data object Settings : Screen
}
