# Validation log

Status date: 2026-07-23 (Asia/Shanghai)

## Verified environment

| Item | Verified value |
|---|---|
| macOS | 26.5.2 (25F84), arm64 |
| Project JDK | Homebrew OpenJDK 17.0.17 |
| Gradle | 8.7 |
| Android Gradle Plugin | 8.6.1 |
| Kotlin | 2.1.21 |
| KSP | 2.1.21-2.0.1 |
| Kuikly | 2.23.2-2.1.21 |
| Android SDK | API 34, Build Tools 34.0.0 |
| Android emulator | 36.6.11 |
| Android system image | API 34 Google APIs ARM64 r14 |
| Android AVD | Pixel 7 profile, Android 14, arm64-v8a |
| Xcode | 26.0.1 (17A400) |
| iOS simulator | iPhone 17 Pro, iOS 26.0 |
| CocoaPods | 1.17.0, project-local Bundler install |

The global default Java, global Ruby gems, Xcode selection, Git configuration,
and signing settings were not changed.

## Automated and build verification

All Gradle commands use JDK 17. iOS commands additionally use:

```bash
export DEVELOPER_DIR=/Applications/Xcode.app/Contents/Developer
```

Android commands additionally use the user SDK through `ANDROID_SDK_ROOT` and
`ANDROID_HOME`.

| Command | Result |
|---|---|
| `./gradlew tasks` | Pass |
| `./gradlew :loading-kit:allTests` | Pass |
| `./gradlew :shared:compileTestKotlinIosSimulatorArm64` | Pass |
| `./gradlew :shared:linkPodDebugFrameworkIosSimulatorArm64` | Pass |
| `./gradlew :androidApp:assembleDebug` | Pass |
| `bundle exec pod install` from `iosApp/` | Pass, 2 Pods installed |
| `xcodebuild ... -scheme KuiklyLoadingDemo ... build` | Pass |
| `plutil -lint iosApp/KuiklyLoadingDemo/Info.plist` | Pass |

The pure state/Controller suite contains 21 JVM tests: 12 state-machine tests
and 9 Controller tests. The acceptance-scenario parser is also compiled as a
Kotlin/Native test source. Standalone native test executables remain
intentionally skipped because Kuikly core expects the render-host symbol
`com_tencent_kuikly_IsCurrentOnContextThread`; native compatibility is
validated by compilation, framework linking, Pods integration, and the real
iOS host run.

## Real runtime verification

### Android

- Built APK: `androidApp/build/outputs/apk/debug/androidApp-debug.apk`
- Emulator: Android 14, API 34, `arm64-v8a`, 1080 × 2400
- Install: `adb install -r ...` returned `Success`
- Launch: cold start returned `Status: ok`
- Screenshots were captured with `adb exec-out screencap -p`

Verified states:

- interactive gallery;
- full-screen overlay;
- timeout overlay while visible;
- timeout overlay after dismissal, with `Last event: TIMEOUT`;
- custom theme;
- local overlay bounded by its acceptance card.

### iOS

- Host built from `iosApp/KuiklyLoadingDemo.xcworkspace`
- Simulator: iPhone 17 Pro, iOS 26.0, 1206 × 2622
- Install: `xcrun simctl install` passed
- Launch: bundle `io.github.yang1107.KuiklyLoadingDemo` returned a process id
- Screenshots were captured with `xcrun simctl io ... screenshot`

Verified states:

- interactive gallery;
- full-screen overlay;
- timeout overlay while visible;
- timeout overlay after dismissal, with `Last event: TIMEOUT`;
- custom theme;
- local overlay bounded by its acceptance card.

All runtime images were opened and visually inspected after capture. They were
not generated, composited, or substituted.

## Failure and repair record

1. The first state tests failed because the production types did not exist.
   This was the intentional TDD red phase.
2. The original direct Kuikly dependency had no JVM variant. Kuikly UI code
   was isolated in a dedicated source set so the pure JVM test surface remains
   executable.
3. The first iOS host naming used the same module name for app and framework.
   The framework was renamed to `KuiklyLoadingShared`.
4. A standalone native test executable lacked a symbol supplied by the iOS
   render host. The project does not inject a fake production symbol; it uses
   JVM pure tests plus native integration checks.
5. CocoaPods was absent. Version 1.17.0 was installed only in
   `vendor/bundle`, then `pod install` completed.
6. AGP 7.4.2 failed D8 transforms on Kotlin 2.1 bytecode with
   `com.android.tools.r8.kotlin.H`. Android's compatibility matrix requires
   D8/R8 8.6.17 for Kotlin 2.1, so the project moved to AGP 8.6.1 and Gradle
   8.7. The next `assembleDebug` passed.
7. Homebrew's `avdmanager` could not see a separate user SDK root. The same
   official command-line tools were installed inside that SDK root; the AVD
   then created and booted successfully.
8. macOS Computer Use permissions were unavailable. Rather than bypass OS
   permissions, reproducible host launch parameters were added to place the
   real app in each evidence state before normal simulator screenshot capture.

## Evidence paths

Android:

- `artifacts/android/android-gallery.png`
- `artifacts/android/android-full-screen.png`
- `artifacts/android/android-timeout.png`
- `artifacts/android/android-timeout-dismissed.png`
- `artifacts/android/android-custom-theme.png`
- `artifacts/android/android-local.png`

iOS:

- `artifacts/ios/ios-gallery.png`
- `artifacts/ios/ios-full-screen.png`
- `artifacts/ios/ios-timeout.png`
- `artifacts/ios/ios-timeout-dismissed.png`
- `artifacts/ios/ios-custom-theme.png`
- `artifacts/ios/ios-local.png`

## Remaining manual boundaries

- The screenshots prove rendered states and timeout dismissal, but not the
  subjective smoothness of the fade animation.
- `blockTouch=false` compiles and is demonstrated in the gallery; a human tap
  pass-through session was not automated because macOS UI-control permission
  was unavailable.
- Project-side acceptance and activity certificate registration remain
  external actions. No GitHub repository, push, release, or Issue completion
  comment has been created by this local run.
