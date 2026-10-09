package com.example.platform

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.view.accessibility.AccessibilityManager

class AccessibilityStatusProvider(private val context: Context) {

    fun isAccessibilityServiceEnabled(): Boolean {
        val expectedComponentName = ComponentName(context, VeilAccessibilityService::class.java)

        // Primary check: AccessibilityManager enabled services list
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
        if (am != null) {
            val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            for (service in enabledServices) {
                val serviceInfo = service.resolveInfo?.serviceInfo
                if (serviceInfo != null &&
                    serviceInfo.packageName == expectedComponentName.packageName &&
                    serviceInfo.name == expectedComponentName.className
                ) {
                    return true
                }
            }
        }

        // Secondary fallback: Settings.Secure enabled_accessibility_services string
        try {
            val enabledServicesSetting = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false

            val colonSplitter = enabledServicesSetting.split(":")
            val flatName = expectedComponentName.flattenToString()
            val shortName = expectedComponentName.flattenToShortString()
            return colonSplitter.any { it.equals(flatName, ignoreCase = true) || it.equals(shortName, ignoreCase = true) }
        } catch (_: Exception) {
            return false
        }
    }
}
