package com.example

import android.content.Context
import com.example.data.DataStoreSettingsRepository
import com.example.data.SettingsRepository
import com.example.data.dataStore
import com.example.platform.AccessibilityStatusProvider
import com.example.platform.CompatibilityChecker
import com.example.platform.ServiceStateHolder

class AppContainer(context: Context) {
    val settingsRepository: SettingsRepository = DataStoreSettingsRepository(context.dataStore, context)
    val serviceStateHolder: ServiceStateHolder = ServiceStateHolder()
    val accessibilityStatusProvider: AccessibilityStatusProvider = AccessibilityStatusProvider(context)
    val compatibilityChecker: CompatibilityChecker = CompatibilityChecker
}
