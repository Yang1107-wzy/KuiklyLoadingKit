# Project memory

## Objective

Implement Tencent-TDS/KuiklyUI Issue #1480 as an independent public-ready
Kuikly loading component repository, with full-screen/local modes, controller,
timeout, DSL, examples, documentation, and Android/iOS evidence.

## Current modules

- `loading-kit`: reusable component, state machine, Controller, tests;
- `shared`: Kuikly gallery and KMP/CocoaPods framework;
- `androidApp`: minimal Android render host;
- `iosApp`: minimal iOS render host;
- `docs`: API, architecture, state, decisions, environment, build, validation;
- `artifacts`: real simulator screenshots only.

## Versions

- Kuikly 2.23.2-2.1.21
- Kotlin 2.1.21
- KSP 2.1.21-2.0.1
- AGP 8.6.1
- Gradle 8.7
- JDK 17
- CocoaPods 1.17.0

## Verified commands

```bash
./gradlew :loading-kit:allTests
./gradlew :androidApp:assembleDebug
./gradlew :shared:compileTestKotlinIosSimulatorArm64
./gradlew :shared:linkPodDebugFrameworkIosSimulatorArm64
bundle exec pod install
xcodebuild -workspace iosApp/KuiklyLoadingDemo.xcworkspace \
  -scheme KuiklyLoadingDemo \
  -configuration Debug \
  -destination 'platform=iOS Simulator,name=iPhone 17 Pro' \
  CODE_SIGNING_ALLOWED=NO build
```

## Verified platforms

- Android 14 / API 34 / ARM64 emulator;
- iPhone 17 Pro / iOS 26.0 simulator;
- JVM pure state/Controller tests.

## Current boundary

Local implementation, automated tests, APK, Pods integration, iOS host build,
both simulator runs, and screenshot evidence are complete. Human touch
pass-through and animation smoothness remain manual observations.

No remote, push, release, or Issue completion comment has been created. Those
external writes require a separate exact confirmation.

## Next actions

1. Perform final clean verification and source/link/secret checks.
2. Review the final local diff and commit the runtime-evidence change.
3. Present the exact repository/push/Issue-comment actions for confirmation.
4. After confirmation, create or connect the public repository, push, release,
   submit the Issue completion comment, and request explicit maintainer
   acceptance.
