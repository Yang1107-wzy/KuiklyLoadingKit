# KuiklyLoadingKit

KuiklyLoadingKit is an independent implementation for
[Tencent-TDS/KuiklyUI issue #1480](https://github.com/Tencent-TDS/KuiklyUI/issues/1480).
It provides full-screen and parent-bounded loading overlays, a deterministic
controller/state machine, timeout replacement safety, lifecycle cleanup,
mode-specific themes, touch policy, fade transitions, tests, and an
interactive Kuikly gallery.

## Verified status

As of 2026-07-23, the 21 state/Controller tests, Android Debug APK, iOS
Simulator ARM64 framework, CocoaPods integration, and the iOS Xcode workspace
build all pass. The app was cold-started on Android 14 API 34 ARM64 and iPhone
17 Pro iOS 26.0 simulators. Real gallery, full-screen, timeout before/after,
custom-theme, and local-overlay screenshots are stored under `artifacts/`.
See [docs/VALIDATION.md](docs/VALIDATION.md).

## Usage

```kotlin
val controller = LoadingController()

LoadingOverlay(controller) {
    attr {
        mode = LoadingMode.FULL_SCREEN
        defaultMessage = "Loading…"
        blockTouch = true
        animation {
            enabled = true
            durationMillis = 200L
        }
    }
    event {
        onDismiss { reason ->
            println(reason)
        }
    }
}

controller.show(
    message = "Fetching data",
    timeoutMillis = 10_000L,
)
controller.updateMessage("Processing")
controller.hide()
```

`timeoutMillis == null`, zero, or a negative value means no automatic
dismissal. `showDefaults()` is the explicit API that consumes DSL defaults.
Repeated `hide()` calls are no-ops.

Attach a `FULL_SCREEN` overlay to the page root. Attach a `LOCAL` overlay to
the bounded parent it should cover. The component cannot render outside its
actual parent layout.

## Build

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export DEVELOPER_DIR=/Applications/Xcode.app/Contents/Developer
export ANDROID_SDK_ROOT="$HOME/Library/Android/sdk"
export ANDROID_HOME="$ANDROID_SDK_ROOT"

./gradlew :loading-kit:desktopTest
./gradlew :loading-kit:allTests
./gradlew :androidApp:assembleDebug
./gradlew :shared:compileKotlinIosSimulatorArm64
./gradlew :shared:linkPodDebugFrameworkIosSimulatorArm64
```

Android is enabled automatically when an SDK path is configured. The iOS app
uses the project-local CocoaPods version locked by `Gemfile.lock`; exact
commands are in [iosApp/README.md](iosApp/README.md).

Project-authored code is licensed under
[Apache License 2.0](LICENSE). Kuikly and other dependencies retain their own
licenses.
