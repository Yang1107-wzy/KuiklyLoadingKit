# KuiklyLoadingKit project rules

## Goal

This repository is an independent implementation for
`Tencent-TDS/KuiklyUI#1480`. It provides a reusable Kuikly loading overlay,
an interactive demo, tests, documentation, and Android/iOS validation
evidence.

## Module boundaries

- `loading-kit/`: reusable component, state machine, controller, scheduling
  abstractions, and unit tests.
- `shared/`: Kuikly demo pages and the cross-platform binary entry.
- `androidApp/`: Android host only.
- `iosApp/`: iOS host only.
- `docs/`: API, architecture, decisions, status, and validation evidence.
- `artifacts/`: real screenshots and recordings captured from running builds.

Do not move component logic into a platform host. Keep the pure state machine
independent from Kuikly UI so it can be tested without a simulator.

## Source and originality

- Do not copy another participant's loading component implementation.
- KuiklyUI and KuiklyChatUI may be inspected only for public APIs, build
  structure, and host integration patterns.
- Do not vendor the KuiklyUI source tree. Depend on official public artifacts.
- Record upstream references and commit SHAs in
  `docs/ORIGINALITY_AND_REFERENCES.md`.

## Verification

Use JDK 17 for Gradle commands without changing the machine-wide Java default.
Discover real Gradle tasks before relying on task names.

Minimum checks before claiming completion:

```text
./gradlew :loading-kit:desktopTest
./gradlew :loading-kit:allTests
./gradlew :shared:compileKotlinIosSimulatorArm64
./gradlew :androidApp:assembleDebug
./gradlew :shared:linkPodDebugFrameworkIosSimulatorArm64
git diff --check
```

Only run Android tasks when a real Android SDK is configured. Only describe a
platform as runtime-verified after the demo has run on a real device or
simulator and evidence has been captured.

## Safety

- Never commit `local.properties`, SDK paths, credentials, tokens, signing
  identities, Apple Team IDs, Pods, DerivedData, build caches, or generated
  binaries.
- Never fabricate screenshots, logs, test results, or platform support.
- Do not change global Git, Java, Xcode, CocoaPods, or Android SDK settings.
- GitHub repository creation, push, tag push, releases, and issue comments
  require the user's explicit confirmation of the exact write.

## Documentation

Public API examples in README and `docs/API.md` must match compiling code.
Update `docs/IMPLEMENTATION_STATUS.md`, `docs/DECISIONS.md`, and
`docs/VALIDATION.md` whenever implementation or verified status changes.
