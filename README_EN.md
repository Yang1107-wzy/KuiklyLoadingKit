# KuiklyLoadingKit

KuiklyLoadingKit is a cross-platform loading component for KuiklyUI, developed
for [Tencent-TDS/KuiklyUI #1480](https://github.com/Tencent-TDS/KuiklyUI/issues/1480).
It provides full-screen and parent-bounded overlays, timeout dismissal, a
controller API, a declarative DSL, Android/iOS demos, and automated tests.

## Features

- Full-screen and local loading overlays
- Kuikly `ActivityIndicator`
- `show`, `hide`, `updateMessage`, and `showDefaults`
- Cancellable timeout dismissal
- Request replacement with stale-timeout protection
- Separate themes for full-screen and local modes
- Configurable message, mask, panel, indicator scale, and touch behavior
- Fade transitions and lifecycle cleanup

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

A `null`, zero, or negative timeout disables automatic dismissal. A local
overlay fills its actual parent container.

## Build

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export DEVELOPER_DIR=/Applications/Xcode.app/Contents/Developer
export ANDROID_SDK_ROOT="$HOME/Library/Android/sdk"
export ANDROID_HOME="$ANDROID_SDK_ROOT"

./gradlew :loading-kit:allTests
./gradlew :androidApp:assembleDebug
./gradlew :shared:compileTestKotlinIosSimulatorArm64
./gradlew :shared:linkPodDebugFrameworkIosSimulatorArm64
```

The iOS host setup is documented in [iosApp/README.md](iosApp/README.md).

## Validation

- 21 state-machine and Controller tests
- Android 14 / API 34 ARM64 emulator
- iPhone 17 Pro / iOS 26.0 simulator
- Android APK, Kotlin/Native framework, CocoaPods, and Xcode workspace builds

The command log and runtime screenshots are listed in
[docs/VALIDATION.md](docs/VALIDATION.md).

Project code is licensed under [Apache License 2.0](LICENSE).
