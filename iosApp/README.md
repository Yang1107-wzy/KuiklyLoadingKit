# iOS host

This directory contains the minimal SwiftUI/UIKit host used to run
`LoadingGalleryPage` on an iOS Simulator.

The host deliberately contains no signing team. It uses:

- `OpenKuiklyIOSRender` version `2.23.2`;
- the local `shared` CocoaPod produced by Kotlin Multiplatform;
- framework name `KuiklyLoadingDemo`;
- deployment target iOS 14.1.

After CocoaPods is available:

```bash
cd iosApp
pod install
cd ..
DEVELOPER_DIR=/Applications/Xcode.app/Contents/Developer \
  xcodebuild \
  -workspace iosApp/KuiklyLoadingDemo.xcworkspace \
  -scheme KuiklyLoadingDemo \
  -sdk iphonesimulator \
  -configuration Debug \
  -destination 'platform=iOS Simulator,name=iPhone 17 Pro' \
  CODE_SIGNING_ALLOWED=NO \
  build
```

Do not commit `Pods/`, DerivedData, workspace user data, or signing settings.
