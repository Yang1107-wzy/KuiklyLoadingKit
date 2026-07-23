# Build baseline

Status: cross-platform release candidate verified locally on 2026-07-23.

## Required commands

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export DEVELOPER_DIR=/Applications/Xcode.app/Contents/Developer
export ANDROID_SDK_ROOT="$HOME/Library/Android/sdk"
export ANDROID_HOME="$ANDROID_SDK_ROOT"
export PATH="/opt/homebrew/opt/ruby/bin:$PATH"

./gradlew tasks
./gradlew :loading-kit:allTests
./gradlew :androidApp:assembleDebug
./gradlew :shared:compileTestKotlinIosSimulatorArm64
./gradlew :shared:linkPodDebugFrameworkIosSimulatorArm64

cd iosApp
bundle exec pod install
cd ..

xcodebuild \
  -workspace iosApp/KuiklyLoadingDemo.xcworkspace \
  -scheme KuiklyLoadingDemo \
  -configuration Debug \
  -destination 'platform=iOS Simulator,name=iPhone 17 Pro' \
  -derivedDataPath build/ios-derived \
  CODE_SIGNING_ALLOWED=NO \
  build
```

All commands above have passed on the environment documented in
`docs/ENVIRONMENT.md`.

## Runtime baseline

Android:

```bash
adb install -r androidApp/build/outputs/apk/debug/androidApp-debug.apk
adb shell am start -W \
  -n io.github.yang1107.kuikly.loading.demo/.MainActivity
```

iOS:

```bash
xcrun simctl install booted \
  build/ios-derived/Build/Products/Debug-iphonesimulator/KuiklyLoadingDemo.app
xcrun simctl launch booted io.github.yang1107.KuiklyLoadingDemo
```

Both hosts have been installed and cold-started successfully on ARM64
simulators. Reproducible evidence launch parameters are documented beside the
screenshots in `artifacts/android/README.md` and `artifacts/ios/README.md`.
