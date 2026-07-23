# Build

## Environment variables

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export DEVELOPER_DIR=/Applications/Xcode.app/Contents/Developer
export ANDROID_SDK_ROOT="$HOME/Library/Android/sdk"
export ANDROID_HOME="$ANDROID_SDK_ROOT"
```

## Gradle

```bash
./gradlew :loading-kit:allTests
./gradlew :androidApp:assembleDebug
./gradlew :shared:compileTestKotlinIosSimulatorArm64
./gradlew :shared:linkPodDebugFrameworkIosSimulatorArm64
```

## CocoaPods

```bash
./gradlew :shared:generateDummyFramework
bundle config set --local path vendor/bundle
bundle install

cd iosApp
bundle exec pod install
cd ..
```

## Xcode

```bash
xcodebuild \
  -workspace iosApp/KuiklyLoadingDemo.xcworkspace \
  -scheme KuiklyLoadingDemo \
  -configuration Debug \
  -destination 'platform=iOS Simulator,name=iPhone 17 Pro' \
  -derivedDataPath build/ios-derived \
  CODE_SIGNING_ALLOWED=NO \
  build
```

## Run

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
