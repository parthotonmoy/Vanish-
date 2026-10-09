# Veil — Android Keyboard Privacy Veil

Veil is a native Android utility that displays a configurable, translucent visual band over the area where typed text appears whenever the system on-screen keyboard is active, and removes it immediately when the keyboard closes. Touches pass directly through the band, enabling uninterrupted typing and interaction.

---

## 1. Key Features

- **Automatic Activation:** Follows the system keyboard in real time across any app.
- **Pass-Through Touches:** Full interaction pass-through using native Android window manager flags (`FLAG_NOT_TOUCHABLE`, `FLAG_NOT_FOCUSABLE`).
- **Precision Controls:**
  - **Opacity:** 10% to 80% (default 55%), compliant with Android 12+ touch obscuration policies.
  - **Coverage:** 10% to 50% of display height (default 25%), with Compact, Standard, and Tall presets.
  - **Keyboard Overlap:** 0% to 60% overlap into the keyboard header (default 30%).
- **Interactive Live Preview:** Real-time schematic screen showing exact band placement with Light and Dark keyboard silhouettes.
- **Troubleshooting Suite:** In-app interactive test field to summon the keyboard and verify overlay performance.
- **Zero Network & Zero Logging:** Strictly zero permissions in AndroidManifest.xml. Veil never reads, stores, or transmits keystrokes or screen content.

---

## 2. Privacy & Permission Model

- **No `<uses-permission>` tags** in the application manifest.
- **No Network Access:** `INTERNET` permission is absent.
- **No Node Reading:** Veil never accesses `rootInActiveWindow`, `AccessibilityNodeInfo`, or event text. It only inspects the coordinate frame (`getBoundsInScreen`) of `TYPE_INPUT_METHOD` windows.

---

## 3. Installation & Setup

1. Install the APK:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```
2. Open Veil.
3. Review the Privacy Disclosure and tap **Set up**.
4. Enable the **Veil** service under **System Settings → Accessibility**.
   - *Android 13+ Note:* If the toggle is disabled ("Restricted setting"), navigate to **Settings → Apps → Veil → tap top-right menu → Allow restricted settings**, then return to Accessibility settings.
5. Return to Veil and verify that the status indicator reflects **Ready**.
6. Tap the **Interactive test field** on the Troubleshooting screen to observe the privacy band.

---

## 4. Build Commands

```bash
# Verify unit tests
gradle :app:testDebugUnitTest

# Build debug APK
gradle :app:assembleDebug

# Build release APK
gradle :app:assembleRelease
```

---

## 5. Developer Attribution

- **Developer:** Mahfuz Alam Tonmoy
- **University:** Bangladesh University of Engineering and Technology (BUET)
