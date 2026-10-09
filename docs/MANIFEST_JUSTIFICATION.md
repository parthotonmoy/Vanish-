# Veil — Manifest & Permissions Justification

*Android keyboard privacy veil · Manifest analysis & zero-permission model · October 2026*

## 1. Zero Requested Permissions

Veil requests **zero `<uses-permission>` tags** in its `AndroidManifest.xml`.

| Permission | Status | Justification |
|---|---|---|
| `android.permission.INTERNET` | **Omitted** | Veil performs zero network communication. Privacy guarantee is strictly enforced at the OS sandbox level. |
| `android.permission.SYSTEM_ALERT_WINDOW` | **Omitted** | Veil uses `WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY` provided natively by the accessibility service. No "Display over other apps" permission is needed. |
| `android.permission.POST_NOTIFICATIONS` | **Omitted** | Veil does not run a foreground service and posts no notifications. |
| `android.permission.FOREGROUND_SERVICE` | **Omitted** | Not required. Android binds and retains an enabled AccessibilityService directly. |
| `android.permission.RECEIVE_BOOT_COMPLETED` | **Omitted** | Android system automatically restores enabled AccessibilityServices after device reboot. |
| `android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | **Omitted** | Promptly rejected. Veil directs users to system App Info if OEM background management causes issues. |

---

## 2. Declared Components

1. **`com.example.MainActivity`**
   - Main entry point for user configuration, setup wizard, interactive test field, and settings.
   - Declares `android:exported="true"` with `CATEGORY_LAUNCHER`.
   - Uses `android:windowSoftInputMode="adjustResize"` to accommodate IME testing.

2. **`com.example.platform.VeilAccessibilityService`**
   - Declares `android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"` (required by Android framework to ensure only the system can bind to the service).
   - Exported with the standard `android.accessibilityservice.AccessibilityService` action.
   - Points to `@xml/accessibility_service_config`.

3. **Backup & Data Extraction Rules**
   - `android:allowBackup="false"` to prevent extracting user preference data across devices.
