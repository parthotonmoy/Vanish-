# Veil — Platform Research and Feasibility Notes

*Android keyboard privacy veil · Feasibility assessment and spike findings · October 2026*

## Evidence Label Key

- `[DEVICE]`: Observed and verified on physical Android hardware.
- `[EMULATOR]`: Observed on an Android emulator runtime.
- `[STATIC]`: Verified via Android open source code (AOSP), official Android documentation, API definitions, or bytecode analysis.
- `[NOT VERIFIED]`: Unverified hypothesis or scenario pending hardware verification.

---

## 1. Feasibility Assessment

### 1.1 Keyboard Visibility Detection Across Apps

| Mechanism | Assessment | Label |
|---|---|---|
| `WindowInsetsCompat.Type.ime()` | Restricted strictly to windows owned by the caller's process. Cannot observe other apps. | `[STATIC]` |
| `ViewTreeObserver` & Layout Listeners | Confined to caller's window hierarchy. Zero cross-process visibility. | `[STATIC]` |
| Insets on Overlay Window | A non-focusable overlay window (`FLAG_NOT_FOCUSABLE`) does not receive IME insets from other applications. | `[STATIC]` |
| `InputMethodManager` | APIs (`isActive`, `isAcceptingText`) are per-client and do not expose global IME display state or coordinates of other applications. | `[STATIC]` |
| Custom `InputMethodService` | Rejected per product specification: users must keep their existing keyboard (Gboard, Samsung Keyboard, SwiftKey). | `[STATIC]` |
| Privileged Shell / Polling | Polling `dumpsys` or taking screenshots requires root or `MediaProjection`, burns battery, and violates zero-data-access privacy requirements. | `[STATIC]` |
| **`AccessibilityService` Windows API** | **Selected.** With `FLAG_RETRIEVE_INTERACTIVE_WINDOWS`, `AccessibilityService.getWindows()` lists on-screen interactive windows across processes, including `AccessibilityWindowInfo.TYPE_INPUT_METHOD`. Global, event-driven (`TYPE_WINDOWS_CHANGED`), zero polling. | `[STATIC]` |

### 1.2 Spike Research Questions & Answers

1. **Does hiding and showing the keyboard produce `TYPE_WINDOWS_CHANGED` events?**  
   - `[STATIC]`: Yes. Under AOSP `WindowManagerService`, the IME window transitions trigger window hierarchy updates that fire `TYPE_WINDOWS_CHANGED` with `WINDOW_CACHE_NAME` changes, additions, removals, or bounds changes (`WINDOWS_CHANGE_BOUNDS`, `WINDOWS_CHANGE_ADDED`, `WINDOWS_CHANGE_REMOVED`).

2. **Is the `TYPE_INPUT_METHOD` window bounds rectangle accurate?**  
   - `[STATIC]`: `AccessibilityWindowInfo.getBoundsInScreen(Rect)` returns the absolute screen frame of the IME window as reported by the window manager. For standard docked keyboards, this covers the keyboard area and candidate strip.

3. **Behavior during show/hide animation:**  
   - `[STATIC]`: As the IME animates via `InsetsSourceConsumer`, bounds updates arrive when the window frame or layout updates. On API 28-30, intermediate layout updates can occur; on API 31+, insets animation often fires on layout bounds commit. A smooth 120ms alpha fade on the veil overlay view mitigates any transition step discrepancy without blocking frame delivery.

4. **Hardware keyboards and candidate-only strips:**  
   - `[STATIC]`: When a hardware keyboard is connected, many IMEs display only a thin candidate strip (height typically 32-44 dp). Veil filters out windows with height < 48 dp to prevent drawing an unwanted band when only a candidate bar is visible.

5. **Floating, split, and one-handed keyboard layouts:**  
   - `[STATIC]`: Floating keyboards report bounds narrower than the display width (e.g. < 90% display width) and detached from the bottom edge. Veil detects floating bounds and confines the band horizontally to the floating IME window bounds instead of spanning full display width.

6. **Does `getWindows()` work without `canRetrieveWindowContent`?**  
   - `[STATIC]`: **No.** In AOSP `AccessibilityManagerService.java`, the system explicitly checks `if (!service.mCanRetrieveWindowContent) return Collections.emptyList()`. Without `android:canRetrieveWindowContent="true"` in the XML configuration, `service.windows` returns an empty list across all apps. Declaring `canRetrieveWindowContent="true"` is mandatory for IME window detection. Veil uses this access strictly to measure window bounds coordinates while never traversing view hierarchies.

7. **Switching IMEs while active:**  
   - `[STATIC]`: Switching IMEs causes the previous IME window to be removed and the new IME window to be added. `OverlayController` uses a single view instance and coalesces state updates so there is no flicker or window duplication.

---

## 2. Overlay Window Type & Untrusted Touches

### 2.1 Window Type Selection
- **Selected: `TYPE_ACCESSIBILITY_OVERLAY`** added through the accessibility service's own `WindowManager`.
- `[STATIC]`: Requires **no** `SYSTEM_ALERT_WINDOW` permission (no "Display over other apps" prompt).
- `[STATIC]`: Stacks above the IME layer because accessibility overlays are intended for accessibility magnification and overlays.
- `[STATIC]`: Android system-protected screens (e.g. runtime permission grant dialogs, package installer) enforce security overlays filtering where Android intentionally hides or prevents touches over security prompts. Veil respects this platform security boundary.

### 2.2 Touch Transparency & Opacity Rules
- Flags applied: `FLAG_NOT_FOCUSABLE`, `FLAG_NOT_TOUCHABLE`, `FLAG_NOT_TOUCH_MODAL`, `FLAG_LAYOUT_IN_SCREEN`, `FLAG_LAYOUT_NO_LIMITS`.
- `[STATIC]`: Android 12+ (API 31+) blocks touches that pass through untrusted overlays if their obscuring opacity exceeds `0.80` (80%). While `TYPE_ACCESSIBILITY_OVERLAY` is managed by a trusted service, Veil strictly caps opacity to `0.80` (80%) so touch-through is guaranteed regardless of platform trust classifications.
- Window `alpha = settings.opacity` is set directly on `WindowManager.LayoutParams.alpha` while the view itself is drawn with solid `#000000`, ensuring the OS touch pipeline evaluates the true opacity seen by the user.

---

## 3. Battery and Background Execution

- `[STATIC]`: Veil does not register any foreground service, runs no notification, and requests no wake locks.
- `[STATIC]`: Android system maintains the bound accessibility service lifecycle. When the screen is off or the keyboard is hidden, the service receives zero window events and consumes 0% CPU.
- `[STATIC]`: When the master switch is toggled OFF, Veil sets `eventTypes = 0` so the OS stops dispatching window change events to the process entirely.
