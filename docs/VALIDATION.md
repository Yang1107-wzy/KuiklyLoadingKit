# Validation

验证日期：2026-07-23

## 测试环境

| 项目 | 版本 |
|---|---|
| macOS | 26.5.2 (25F84), arm64 |
| JDK | OpenJDK 17.0.17 |
| Gradle | 8.7 |
| Android Gradle Plugin | 8.6.1 |
| Kotlin | 2.1.21 |
| KSP | 2.1.21-2.0.1 |
| Kuikly | 2.23.2-2.1.21 |
| Android SDK | API 34, Build Tools 34.0.0 |
| Android Emulator | 36.6.11 |
| Xcode | 26.0.1 (17A400) |
| CocoaPods | 1.17.0 |

## 构建与测试

```bash
./gradlew --no-build-cache --rerun-tasks \
  :loading-kit:allTests \
  :androidApp:assembleDebug \
  :shared:compileTestKotlinIosSimulatorArm64 \
  :shared:linkPodDebugFrameworkIosSimulatorArm64
```

该命令共执行 102 个 Gradle 任务，结果为 `BUILD SUCCESSFUL`。

| 检查项 | 结果 |
|---|---|
| 状态机测试 | 12 项通过 |
| Controller 测试 | 9 项通过 |
| Android Debug APK | 通过 |
| iOS Simulator 测试源码编译 | 通过 |
| iOS Simulator Framework 链接 | 通过 |
| CocoaPods 安装 | 2 个 Pod 安装成功 |
| Xcode workspace 构建 | 通过 |
| `Info.plist` 校验 | 通过 |

`loading-kit:allTests` 运行纯 Kotlin 状态机和 Controller 测试。iOS 端通过
Kotlin/Native 编译、Framework 链接、CocoaPods 集成和宿主运行检查兼容性。

## Android

- 设备：Pixel 7 模拟器
- 系统：Android 14，API 34，`arm64-v8a`
- 分辨率：1080 × 2400
- APK：`androidApp/build/outputs/apk/debug/androidApp-debug.apk`

已运行的场景：

- Demo 列表
- 全屏 Loading
- 局部 Loading
- 超时关闭前后
- 自定义主题

截图位于 [artifacts/android](../artifacts/android/)。

## iOS

- 设备：iPhone 17 Pro 模拟器
- 系统：iOS 26.0
- 分辨率：1206 × 2622
- 宿主：`iosApp/KuiklyLoadingDemo.xcworkspace`

已运行的场景：

- Demo 列表
- 全屏 Loading
- 局部 Loading
- 超时关闭前后
- 自定义主题

截图位于 [artifacts/ios](../artifacts/ios/)。

## 截图索引

| 场景 | Android | iOS |
|---|---|---|
| Gallery | [图片](../artifacts/android/android-gallery.png) | [图片](../artifacts/ios/ios-gallery.png) |
| Full screen | [图片](../artifacts/android/android-full-screen.png) | [图片](../artifacts/ios/ios-full-screen.png) |
| Local | [图片](../artifacts/android/android-local.png) | [图片](../artifacts/ios/ios-local.png) |
| Timeout | [关闭前](../artifacts/android/android-timeout.png) / [关闭后](../artifacts/android/android-timeout-dismissed.png) | [关闭前](../artifacts/ios/ios-timeout.png) / [关闭后](../artifacts/ios/ios-timeout-dismissed.png) |
| Custom theme | [图片](../artifacts/android/android-custom-theme.png) | [图片](../artifacts/ios/ios-custom-theme.png) |

## 补充说明

- 截图记录了主要显示状态和超时关闭结果。
- 淡入淡出动画已接入组件，当前仓库未提供录屏。
- `blockTouch` 已接入 Demo，未单独提供触摸穿透录屏。
