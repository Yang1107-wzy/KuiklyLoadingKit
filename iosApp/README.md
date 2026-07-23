# iOS Demo

`iosApp` 是运行 `LoadingGalleryPage` 的 iOS 示例宿主。

- Deployment target：iOS 14.1
- Render：`OpenKuiklyIOSRender` 2.23.2
- Kotlin framework：`KuiklyLoadingShared`
- App target：`KuiklyLoadingDemo`

## Install Pods

在仓库根目录执行：

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export DEVELOPER_DIR=/Applications/Xcode.app/Contents/Developer

./gradlew :shared:generateDummyFramework
bundle config set --local path vendor/bundle
bundle install

cd iosApp
bundle exec pod install
cd ..
```

## Build

```bash
xcodebuild \
  -workspace iosApp/KuiklyLoadingDemo.xcworkspace \
  -scheme KuiklyLoadingDemo \
  -configuration Debug \
  -destination 'platform=iOS Simulator,name=iPhone 17 Pro' \
  CODE_SIGNING_ALLOWED=NO \
  build
```
