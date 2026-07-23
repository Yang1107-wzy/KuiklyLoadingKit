# Engineering decisions

## 2026-07-23: version baseline

- Kuikly: `2.23.2-2.1.21`
- Kotlin: `2.1.21`
- KSP: `2.1.21-2.0.1`
- Android Gradle Plugin: `7.4.2`
- Gradle: `7.6.3`

Kuikly `2.23.2` is the latest official GitHub release observed during Gate 0,
and the Tencent Maven metadata exposes `2.23.2-2.1.21` as the latest released
`core` artifact. The Gradle, AGP, Kotlin, and KSP versions follow the Kuikly
`2.23.2` source configuration.

## 2026-07-23: Android configuration without an SDK

The inspected machine has no Android SDK. The project therefore enables its
Android targets and includes `androidApp` only when a valid SDK path is
provided through `ANDROID_SDK_ROOT`, `ANDROID_HOME`, `local.properties`, or
`-PandroidSdkPath`. This keeps common/JVM/iOS work testable without claiming an
Android build. On a normal Android development machine, Android is enabled
automatically.

## 2026-07-23: pure state machine

The request lifecycle is implemented independently from Kuikly UI. UI code
consumes state snapshots and side effects. This permits deterministic timeout,
replacement, idempotency, and lifecycle tests without a simulator.

## 2026-07-23: custom `kuiklyMain` source set

Kuikly `core` has Android and native variants but no conventional JVM variant
for the desktop test target. An initial direct `commonMain` dependency made
desktop test resolution fail. The final layout keeps pure logic in
`commonMain` and places Kuikly UI in `kuiklyMain`, which Android and each iOS
main source set consume. The default KMP hierarchy template is explicitly
disabled because these source-set edges are intentional.

## 2026-07-23: default timeout is explicit

Issue semantics require `show(timeoutMillis = null)` to mean no automatic
dismissal, while the desired DSL also exposes `defaultTimeoutMillis`. To avoid
silently changing null semantics, the default timeout is consumed only by
`showDefaults()`. `show()` never substitutes it.

## 2026-07-23: animation remains presentation-only

The overlay stays attached, uses reactive opacity for fade in/out, and disables
touch while hidden. No animation completion callback changes the request
state. This avoids introducing SHOWING/HIDING states that could race with
rapid show/hide calls.

## 2026-07-23: actual CocoaPods framework task

After applying Kotlin's CocoaPods plugin, the real simulator framework task is
`:shared:linkPodDebugFrameworkIosSimulatorArm64`, not the initially proposed
`:shared:linkDebugFrameworkIosSimulatorArm64`. Validation and public commands
use the discovered task.

## 2026-07-23: pure tests run on JVM

The state/controller tests do not use Kuikly UI, but Kotlin's native test
executable still links the iOS main binary. Kuikly core expects
`com_tencent_kuikly_IsCurrentOnContextThread`, which is supplied by the native
render host, not by a standalone test executable. The attempted
`iosSimulatorArm64Test` therefore failed at ld with that undefined symbol.

`loading-kit:allTests` is explicitly defined as the deterministic JVM suite.
iOS compatibility is checked separately through target compilation and the
real CocoaPods framework link. This avoids adding a fake renderer symbol just
to make tests link.

## 2026-07-23: iOS render version

The CocoaPods trunk API listed `OpenKuiklyIOSRender` `2.23.2`, matching the
selected Kuikly release. The Podfile pins that exact version.
