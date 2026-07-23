# Contributing

Contributions should remain within the issue scope and preserve the separation
between pure state logic, Kuikly UI, and platform hosts.

Before submitting a change:

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
./gradlew :loading-kit:desktopTest
./gradlew :loading-kit:allTests
git diff --check
```

When Apple tooling is available, also compile and link the iOS Simulator ARM64
framework. When Android tooling is available, build and run `androidApp`.

Do not commit SDK paths, `local.properties`, credentials, signing identities,
Pods, DerivedData, build caches, generated screenshots, or copied participant
implementations. Runtime evidence must come from the actual demo.

Use focused commits with prefixes such as `feat:`, `fix:`, `test:`, `docs:`,
and `chore:`.
