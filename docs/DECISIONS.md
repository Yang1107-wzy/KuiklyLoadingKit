# Engineering decisions

## 2026-07-23: version baseline

- Kuikly: `2.23.2-2.1.21`
- Kotlin: `2.1.21`
- KSP: `2.1.21-2.0.1`
- Android Gradle Plugin: `8.6.1`
- Gradle: `8.7`

Kuikly `2.23.2` is the latest official GitHub release observed during Gate 0,
and the Tencent Maven metadata exposes `2.23.2-2.1.21` as the latest released
`core` artifact. The Gradle, AGP, Kotlin, and KSP versions follow the Kuikly
`2.23.2` source configuration. The first Android attempt used AGP 7.4.2, but
its bundled D8 could not transform Kotlin 2.1 bytecode. Android's compatibility
matrix requires D8/R8 8.6.17 for Kotlin 2.1, which is supplied by AGP 8.6.
AGP 8.6 in turn requires Gradle 8.7 and JDK 17. The complete wrapper was
regenerated with Gradle 8.7, and its distribution ZIP is protected by the
official SHA-256 checksum.

## 2026-07-23: conditional Android configuration

The project enables its Android targets and includes `androidApp` only when a
valid SDK path is provided through `ANDROID_SDK_ROOT`, `ANDROID_HOME`, or
`local.properties`. The configured directory must exist. This keeps
common/JVM/iOS work testable on machines without an SDK. The final validation
machine now has a user-local API 34 SDK and ARM64 emulator, so Android is
enabled and verified.

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

## 2026-07-23: distinct iOS module names

The application target remains `KuiklyLoadingDemo`; the Kotlin framework is
named `KuiklyLoadingShared`. Keeping them distinct avoids a Swift module-name
collision after CocoaPods integrates the framework into the app target.

## 2026-07-23: reproducible acceptance launch states

macOS Computer Use permission was not available for automated Simulator clicks.
The implementation does not bypass that OS boundary. Android Intent extras and
iOS process arguments select one of four deterministic evidence states:
`full-screen`, `timeout`, `custom-theme`, or `local`. Each state still runs the
real Kuikly host and component; normal launches continue to open the complete
interactive gallery.
