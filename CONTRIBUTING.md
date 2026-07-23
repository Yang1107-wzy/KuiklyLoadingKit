# Contributing

Changes can be submitted through a focused pull request. The main checks are:

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home

./gradlew :loading-kit:allTests
./gradlew :androidApp:assembleDebug
git diff --check
```

The reusable component belongs in `loading-kit`; platform-specific host code
belongs in `androidApp` or `iosApp`. Generated build output, SDK paths,
credentials, signing files, Pods, and DerivedData are excluded from version
control.
