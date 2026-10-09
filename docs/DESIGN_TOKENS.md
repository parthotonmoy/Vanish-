# Veil — Design Tokens & Contrast Validation

*Android keyboard privacy veil · Design tokens, color system and WCAG contrast ratios · October 2026*

## 1. Design Philosophy

Veil adopts an aesthetic of precision technical instrumentation:
- Translucent optical filter metaphor.
- Restrained visual hierarchy with generous negative space.
- Zero decorative clutter, gradients, fake metrics, or rounded card bloat.
- Flat tonal surfaces with 1 dp dividers and standard 8 dp layout grids.

---

## 2. Color Tokens

All colors are strictly managed through `VeilColors` and referenced via `VeilTheme.colors`. No `Color(0x...)` literals exist in screen Composables.

| Token | Dark Mode | Light Mode | Primary Usage |
|---|---|---|---|
| `background` | `#0C1015` | `#F5F7FA` | Full-bleed screen background |
| `surface` | `#141B23` | `#FFFFFF` | Banners, dialog surface, text field background |
| `surfaceElevated` | `#1B2530` | `#EDF1F6` | Preview container well, segmented button tracks, snackbars |
| `accent` | `#8CB7FF` | `#285DB8` | Switch on-state, active sliders, primary actions, focus rings |
| `accentSecondary` | `#7ADBC8` | `#147D70` | Veil coverage outline in live preview |
| `textPrimary` | `#F0F4F8` | `#17212B` | Screen titles, numeric readouts, setting labels |
| `textSecondary` | `#A7B2C0` | `#586777` | Helper text, secondary descriptions, inactive icons |
| `divider` | `#2A3542` | `#DCE3EB` | Section dividers, borders, inactive slider tracks |
| `success` | `#7ED6A5` | `#217A4B` | Active / connected service indicators |
| `warning` | `#F1C879` | `#8C5C0A` | Setup needed state |
| `error` | `#FF8C8C` | `#B3261E` | Disconnected or failed state |
| `onAccent` | `#0C1015` | `#FFFFFF` | Text and icons placed on `accent` fill |
| `veilTint` | `#000000` | `#000000` | Real overlay and preview band color |
| `scrim` | `60% #000000` | `60% #000000` | Modal dialog background scrim |

---

## 3. WCAG Contrast Validation

Requirements: Text and meaningful icons must meet at least **4.5:1** contrast ratio against surrounding surfaces. Large text (≥18sp or 14sp bold) and interactive components must meet at least **3.0:1**.

### 3.1 Dark Theme Ratios

| Foreground Token | Background Surface | Ratio | WCAG 2.1 Result |
|---|---|---|---|
| `textPrimary` (`#F0F4F8`) | `background` (`#0C1015`) | **17.5 : 1** | Pass AAA (≥ 7.0:1) |
| `textPrimary` (`#F0F4F8`) | `surface` (`#141B23`) | **15.2 : 1** | Pass AAA (≥ 7.0:1) |
| `textPrimary` (`#F0F4F8`) | `surfaceElevated` (`#1B2530`) | **13.1 : 1** | Pass AAA (≥ 7.0:1) |
| `textSecondary` (`#A7B2C0`) | `background` (`#0C1015`) | **9.4 : 1** | Pass AAA (≥ 7.0:1) |
| `textSecondary` (`#A7B2C0`) | `surface` (`#141B23`) | **8.2 : 1** | Pass AAA (≥ 7.0:1) |
| `textSecondary` (`#A7B2C0`) | `surfaceElevated` (`#1B2530`) | **7.1 : 1** | Pass AAA (≥ 7.0:1) |
| `accent` (`#8CB7FF`) | `background` (`#0C1015`) | **9.8 : 1** | Pass AAA (≥ 7.0:1) |
| `onAccent` (`#0C1015`) | `accent` (`#8CB7FF`) | **9.8 : 1** | Pass AAA (≥ 7.0:1) |
| `success` (`#7ED6A5`) | `surface` (`#141B23`) | **11.5 : 1** | Pass AAA (≥ 7.0:1) |
| `warning` (`#F1C879`) | `surface` (`#141B23`) | **11.2 : 1** | Pass AAA (≥ 7.0:1) |
| `error` (`#FF8C8C`) | `surface` (`#141B23`) | **7.8 : 1** | Pass AAA (≥ 7.0:1) |

### 3.2 Light Theme Ratios

| Foreground Token | Background Surface | Ratio | WCAG 2.1 Result |
|---|---|---|---|
| `textPrimary` (`#17212B`) | `background` (`#F5F7FA`) | **14.1 : 1** | Pass AAA (≥ 7.0:1) |
| `textPrimary` (`#17212B`) | `surface` (`#FFFFFF`) | **15.2 : 1** | Pass AAA (≥ 7.0:1) |
| `textPrimary` (`#17212B`) | `surfaceElevated` (`#EDF1F6`) | **13.2 : 1** | Pass AAA (≥ 7.0:1) |
| `textSecondary` (`#586777`) | `background` (`#F5F7FA`) | **5.2 : 1** | Pass AA (≥ 4.5:1) |
| `textSecondary` (`#586777`) | `surface` (`#FFFFFF`) | **5.6 : 1** | Pass AA (≥ 4.5:1) |
| `textSecondary` (`#586777`) | `surfaceElevated` (`#EDF1F6`) | **4.9 : 1** | Pass AA (≥ 4.5:1) |
| `accent` (`#285DB8`) | `background` (`#F5F7FA`) | **5.7 : 1** | Pass AA (≥ 4.5:1) |
| `onAccent` (`#FFFFFF`) | `accent` (`#285DB8`) | **5.7 : 1** | Pass AA (≥ 4.5:1) |
| `success` (`#217A4B`) | `surface` (`#FFFFFF`) | **4.8 : 1** | Pass AA (≥ 4.5:1) |
| `warning` (`#8C5C0A`) | `surface` (`#FFFFFF`) | **5.2 : 1** | Pass AA (≥ 4.5:1) |
| `error` (`#B3261E`) | `surface` (`#FFFFFF`) | **5.8 : 1** | Pass AA (≥ 4.5:1) |

Status indications pair distinct icon shapes with text labels, ensuring compliance with non-color accessibility requirements.
