# Validation log

## Gate 0 environment snapshot

Date: 2026-07-23 (Asia/Shanghai)

| Item | Observed state |
|---|---|
| macOS | 26.5.2 (25F84) |
| Architecture | arm64 |
| Default Java | Temurin 11.0.29 |
| Available project Java | Homebrew OpenJDK 17.0.17 |
| Xcode | 26.0.1 via `DEVELOPER_DIR` |
| iOS Simulator | iOS 26.0 devices available |
| CocoaPods | Not installed |
| Android Studio | Not found |
| Android SDK | Not found |
| Git | 2.53.0 |
| GitHub CLI | 2.90.0, authenticated as `Yang1107-wzy` |

Issue #1480 was open at inspection time. Its required scope remained
full-screen/local loading, show/hide plus timeout management, a declarative
DSL, multi-platform support, API documentation, and examples. No maintainer
scope clarification was present in the comments.

## Upstream evidence

- KuiklyUI tag `2.23.2`: commit
  `7afa0f74275211c2b5c8f4610484c2705dd286b0`
- KuiklyUI inspected main: commit
  `b396748db2818fa3beda4e00fa8b0daee19bbb2a`
- KuiklyChatUI inspected main: commit
  `2bb10e88e9c8ab26c2f9d5f2d26f3317c8e376d8`

## Command log

All commands below were executed from the repository root with JDK 17 selected
for the current shell only.

| Command | Exit | Result |
|---|---:|---|
| `./gradlew tasks :loading-kit:tasks :shared:tasks` | 0 | Gradle model and tasks discovered |
| `./gradlew :loading-kit:desktopTest` | 1 then 0 | Expected TDD RED for missing types, then GREEN after implementation |
| `./gradlew :loading-kit:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosSimulatorArm64` | 0 | Initial Kotlin/Native baseline; first run downloaded LLVM/libffi |
| `./gradlew :loading-kit:compileKotlinIosSimulatorArm64 :loading-kit:desktopTest` | 1 then 0 | Public/internal visibility error fixed with private binding adapter |
| `./gradlew :shared:compileKotlinIosSimulatorArm64` | 0 | Gallery plus KSP compilation passed; deprecation warning then removed |
| `xcodebuild -list -project iosApp/KuiklyLoadingDemo.xcodeproj` | 0 | Target and shared scheme recognized |
| `plutil -lint iosApp/KuiklyLoadingDemo/Info.plist` | 0 | `OK` |
| `./gradlew :shared:podspec :shared:linkPodDebugFrameworkIosSimulatorArm64` | 0 | Podspec generated and static simulator framework linked |
| `./gradlew tasks :loading-kit:allTests ...` | 1 | Native test executable lacked a Kuikly render-host symbol; JVM tests passed |

The initial Kotlin/Native run took approximately 4 minutes 21 seconds because
it downloaded the compiler's LLVM and libffi dependencies. Subsequent compiles
were incremental.

## Failure and repair record

1. Gradle 7.6 Kotlin DSL did not accept two APIs used in the first settings
   draft. They were replaced with version-compatible parsing.
2. A direct Kuikly dependency in `commonMain` had no JVM variant. Kuikly UI was
   moved to a dedicated `kuiklyMain` source set.
3. The first state test compilation failed because the types did not exist.
   This was the intentional TDD RED; implementation then made the suite pass.
4. A public View implementing an internal binding exposed internal snapshot
   types. A private adapter now implements the internal interface.
5. The default hierarchy warning was removed by explicitly disabling the
   default template for the intentional custom source-set graph.
6. Deprecated `fontWeightSemisolid()` calls were changed to
   `fontWeightSemiBold()`.
7. A standalone iOS native test executable could not resolve
   `com_tencent_kuikly_IsCurrentOnContextThread`, implemented by
   OpenKuiklyIOSRender. The pure suite is now explicitly JVM-backed, while iOS
   uses compile/framework integration checks rather than a fake test symbol.

## Current build boundary

Verified:

- pure state and Controller tests on JVM;
- loading component compilation for iOS Simulator ARM64;
- Demo/KSP compilation for iOS Simulator ARM64;
- CocoaPods static framework link;
- iOS Xcode project structure and Info.plist parsing.

Not verified:

- `pod install`;
- iOS host build after Pods integration;
- iOS Simulator launch or interaction;
- Android compilation, installation, launch, or interaction;
- touch pass-through behavior on either platform;
- screenshots and GIF.

## Platform blockers

| Platform | Blocker | Consequence |
|---|---|---|
| Android | SDK/platform-tools/emulator/AVD absent | Android target is conditionally disabled; no build/runtime claim |
| iOS | CocoaPods command absent | Framework passes, but host dependencies and runtime are not integrated |

## Evidence paths

- JVM test reports: `loading-kit/build/reports/tests/desktopTest/`
- iOS framework output:
  `shared/build/bin/iosSimulatorArm64/podDebugFramework/`
- Generated local podspec: `shared/shared.podspec`
- Runtime evidence directories: `artifacts/android/`, `artifacts/ios/`,
  `artifacts/demo/` (currently contain status documentation only)

## Acceptance matrix

The current requirement-to-evidence matrix is maintained in
`docs/ISSUE_1480_CHECKLIST.md`. Any runtime row remains Blocked until a real
simulator/device run and evidence capture.
