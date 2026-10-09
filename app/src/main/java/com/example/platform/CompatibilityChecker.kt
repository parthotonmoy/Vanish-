package com.example.platform

import android.os.Build

object CompatibilityChecker {

    val supportsDynamicColor: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val mayRequireRestrictedSettingsNotice: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    const val MAX_UNTRUSTED_OBSCURING_OPACITY: Float = 0.80f

    val sdkInt: Int
        get() = Build.VERSION.SDK_INT
}
