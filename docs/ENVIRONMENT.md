# Environment

Verified on 2026-07-23.

## Toolchain

| Tool | Version |
|---|---|
| macOS | 26.5.2 (25F84), arm64 |
| JDK | Homebrew OpenJDK 17.0.17 |
| Gradle | 8.7 |
| AGP | 8.6.1 |
| Kotlin | 2.1.21 |
| KSP | 2.1.21-2.0.1 |
| Kuikly | 2.23.2-2.1.21 |
| Xcode | 26.0.1 (17A400) |
| CocoaPods | 1.17.0 |

The Gradle 8.7 wrapper files were regenerated with Gradle 8.7. The distribution
is pinned by the official SHA-256 value in
`gradle/wrapper/gradle-wrapper.properties`.

## Android

Installed under the current user's Android SDK directory:

- command-line tools 22.0;
- platform tools 37.0.0;
- platform API 34;
- Build Tools 34.0.0;
- Build Tools 30.0.3 retained as an automatically resolved compatibility
  dependency from the initial AGP run;
- emulator 36.6.11;
- Google APIs API 34 ARM64 system image r14;
- AVD `KuiklyLoadingApi34`, Pixel 7 profile.

The verified guest reports Android 14 and `arm64-v8a`.

## iOS

- CocoaPods gems are isolated in `vendor/bundle`;
- the lock file is `Gemfile.lock`;
- Pods are resolved by `iosApp/Podfile.lock`;
- the generated workspace is
  `iosApp/KuiklyLoadingDemo.xcworkspace`;
- no signing team or private certificate is stored in the project.

## Non-persistent shell selection

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export DEVELOPER_DIR=/Applications/Xcode.app/Contents/Developer
export ANDROID_SDK_ROOT="$HOME/Library/Android/sdk"
export ANDROID_HOME="$ANDROID_SDK_ROOT"
export PATH="/opt/homebrew/opt/ruby/bin:$PATH"
```

These variables are shell-local. The implementation did not modify global Java
selection, Xcode selection, Git settings, or shell startup files.
