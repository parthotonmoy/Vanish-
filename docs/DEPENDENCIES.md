# Veil — Dependency Allowlist & Justification

*Android keyboard privacy veil · Dependency justifications · October 2026*

Every library dependency included in the build is justified with a one-line explanation below. All extraneous dependencies (network, databases, tracking, advertising) have been removed.

| Dependency | Scope | Justification |
|---|---|---|
| `androidx.core:core-ktx` | Implementation | Kotlin extensions for Android platform APIs and system insets handling. |
| `androidx.activity:activity-compose` | Implementation | Integration of Jetpack Compose into Android ComponentActivity with edge-to-edge support. |
| `androidx.compose.bom` | BOM | Coordinates compatible, stable versions across all Jetpack Compose modules. |
| `androidx.compose.ui:ui` | Implementation | Foundational UI toolkit for layouts, Canvas drawing, and measuring. |
| `androidx.compose.ui:ui-graphics` | Implementation | Color models, brush gradients, and vector drawing primitives. |
| `androidx.compose.ui:ui-tooling-preview` | Implementation | Composable preview annotations for tooling. |
| `androidx.compose.material3:material3` | Implementation | Material 3 components (Scaffold, Slider, Switch, Text, Dialogs) adhering to Android standards. |
| `androidx.compose.material:material-icons-core` | Implementation | Standard Material Icons foundation. |
| `androidx.compose.material:material-icons-extended` | Implementation | Extended icons for technical indicators (visibility_off, keyboard, build, etc.). |
| `androidx.datastore:datastore-preferences` | Implementation | Lightweight, asynchronous, corruption-safe persistent storage for user preferences. |
| `androidx.lifecycle:lifecycle-runtime-compose` | Implementation | `collectAsStateWithLifecycle` for lifecycle-aware StateFlow collection without background leaks. |
| `androidx.lifecycle:lifecycle-runtime-ktx` | Implementation | Coroutine lifecycle scopes attached to Android components. |
| `androidx.lifecycle:lifecycle-viewmodel-compose` | Implementation | ViewModel binding and state management inside Compose hierarchies. |
| `kotlinx-coroutines-android` | Implementation | Android Main dispatcher (`Dispatchers.Main.immediate`) for UI and accessibility event handling. |
| `kotlinx-coroutines-core` | Implementation | Asynchronous primitives (`StateFlow`, `SharedFlow`, operators). |
| `junit:junit` | Test | Standard JVM unit test framework. |
| `androidx.test.ext:junit` | Test | AndroidX test runners and assertions. |
| `androidx.test:core` | Test | Core Android testing utilities. |
| `kotlinx-coroutines-test` | Test | Coroutine test dispatchers (`StandardTestDispatcher`, `runTest`) for ViewModel testing. |
| `org.robolectric:robolectric` | Test | JVM-based Android framework simulator for fast local testing without emulators. |
| `androidx.compose.ui:ui-test-junit4` | Test | Compose UI testing and semantic node tree assertions. |
| `androidx.compose.ui:ui-test-manifest` | Debug | Test activity harness for Compose tests. |
| `androidx.compose.ui:ui-tooling` | Debug | Layout inspection and runtime Compose tooling. |

### Prohibited Dependencies Excluded
- No `Room`, `SQLite`, or cloud databases (DataStore Preferences handles all local state).
- No `Retrofit`, `OkHttp`, or `Ktor` (Veil has zero network operations).
- No `Firebase`, `Analytics`, or telemetry SDKs.
- No `Hilt` or `Dagger` (manual constructor injection via single `AppContainer`).
- No image loading libraries (`Coil`, `Glide`) (native vector drawables and Canvas primitives only).
