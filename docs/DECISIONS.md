# Technical decisions

## Version baseline

The project uses Kuikly 2.23.2-2.1.21, Kotlin 2.1.21, KSP
2.1.21-2.0.1, Android Gradle Plugin 8.6.1, Gradle 8.7, and JDK 17.
These versions match the selected Kuikly release and Kotlin 2.1 Android
toolchain requirements.

## Source sets

Request state and Controller logic live in `commonMain`. Kuikly UI code lives
in `kuiklyMain`, which is shared by Android and iOS targets. This keeps the
state machine available to JVM tests without introducing a Kuikly JVM stub.

## Request state

The state machine owns visibility, request replacement, timeout generation,
and dismissal events. Fade animation changes view opacity only and does not
add request states.

## Timeout handling

The active Kuikly timer is cancelled when possible. Each callback also carries
the request generation, so a callback from an older request cannot close the
current one. Long delays are split to fit Kuikly's `Int` timer argument.

## Default timeout

`show(timeoutMillis = null)` means no timeout. DSL defaults are applied only by
`showDefaults()`, keeping the two APIs explicit.

## iOS integration

The Kotlin framework is named `KuiklyLoadingShared`, while the application
target is `KuiklyLoadingDemo`. CocoaPods uses
`OpenKuiklyIOSRender` 2.23.2.
