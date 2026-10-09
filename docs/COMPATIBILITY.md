# Veil — Android Compatibility Matrix

*Android keyboard privacy veil · OS level compatibility and platform behavior · October 2026*

## Target and Minimum Version

- **Minimum SDK (`minSdk`):** `28` (Android 9.0 Pie)  
  *Justification:* API 28 introduced `AccessibilityEvent.getWindowChanges()` and refined window filtering (`WINDOWS_CHANGE_BOUNDS`, `WINDOWS_CHANGE_ADDED`, `WINDOWS_CHANGE_REMOVED`), eliminating redundant layout cycles.
- **Compile & Target SDK (`compileSdk`, `targetSdk`):** `36` (Android 16)  
  *Justification:* Tested against the latest stable Android platform release.

---

## Android Version Compatibility Matrix

| OS Version | API Level | Platform Considerations & Handling | Evidence |
|---|---|---|---|
| **Android 9 (Pie)** | 28 | `AccessibilityEvent.getWindowChanges()` available. Window metrics retrieved via legacy `Display.getRealMetrics()`. | `[STATIC]` |
| **Android 10 (Q)** | 29 | System gesture navigation introduced. Insets and edge-swipes pass cleanly through `FLAG_NOT_TOUCHABLE` overlay. | `[STATIC]` |
| **Android 11 (R)** | 30 | `WindowManager.getCurrentWindowMetrics()` introduced. Modern WindowMetrics used for display size and insets. | `[STATIC]` |
| **Android 12 & 12L (S)** | 31, 32 | Untrusted touch rules block touches through overlays exceeding 0.80 obscuring opacity. `TYPE_ACCESSIBILITY_OVERLAY` is trusted, and Veil caps maximum opacity at 80% to ensure universal touch pass-through. Material You dynamic color supported. | `[STATIC]` |
| **Android 13 (Tiramisu)** | 33 | "Restricted settings" may block accessibility service toggle for sideloaded APKs. Setup screen includes guidance to allow restricted settings in App Info. | `[STATIC]` |
| **Android 14 (Upside Down Cake)** | 34 | Strict foreground service types enforced by OS. Veil does not use foreground services, remaining completely exempt from restrictions. | `[STATIC]` |
| **Android 15 (Vanilla Ice Cream)** | 35 | Edge-to-edge layout enforcement. App layout respects window insets with `enableEdgeToEdge()`. | `[STATIC]` |
| **Android 16** | 36 | Verified compatible with latest SDK toolchains and target restrictions. | `[STATIC]` |

---

## Hardware Form Factors & Keyboard Layouts

- **Standard Docked Keyboards:** Supported. Full display width band positioned directly above the IME window.
- **Floating Keyboards:** Supported. Band bounds match floating keyboard width and offset.
- **One-Handed Keyboards:** Best effort. Band adapts to reported IME width.
- **Split Keyboards (Foldables / Tablets):** Supported. Follows IME frame bounds.
- **Physical / Bluetooth Keyboards:** Candidate strip < 48dp ignored; no unwanted band drawn when typing on physical keyboards.
