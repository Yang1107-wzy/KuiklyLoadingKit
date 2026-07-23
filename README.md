# KuiklyLoadingKit

KuiklyLoadingKit 是一个基于 KuiklyUI 的跨平台 Loading 组件，对应
[Tencent-TDS/KuiklyUI #1480](https://github.com/Tencent-TDS/KuiklyUI/issues/1480)。
组件支持全屏和局部加载、超时关闭、命令式 Controller 与声明式 DSL，并提供
Android、iOS 示例工程和自动化测试。

## 功能

- 全屏遮罩和局部区域两种模式
- Kuikly `ActivityIndicator` 默认加载指示器
- `show`、`hide`、`updateMessage`、`showDefaults`
- 可取消的超时关闭
- 重复 `show` 时的请求替换和旧计时器失效
- 全屏/局部模式独立主题
- 提示文字、遮罩、面板、圆角和指示器缩放配置
- 触摸拦截开关和淡入淡出动画
- 页面卸载与销毁时的资源清理
- 纯 Kotlin 状态机和 Controller 测试

## 运行效果

| Android | iOS |
|---|---|
| ![Android full-screen](artifacts/android/android-full-screen.png) | ![iOS full-screen](artifacts/ios/ios-full-screen.png) |
| ![Android local](artifacts/android/android-local.png) | ![iOS local](artifacts/ios/ios-local.png) |

更多截图见 [artifacts](artifacts/README.md)。

## 基本用法

创建 Controller：

```kotlin
private val loadingController = LoadingController()
```

在页面根容器中声明全屏 Loading：

```kotlin
LoadingOverlay(loadingController) {
    attr {
        mode = LoadingMode.FULL_SCREEN
        defaultMessage = "加载中…"
        blockTouch = true
    }
    event {
        onDismiss { reason ->
            println("dismissed: $reason")
        }
    }
}
```

显示、更新和关闭：

```kotlin
loadingController.show(
    message = "正在请求数据",
    timeoutMillis = 10_000L,
    blockTouch = true,
)

loadingController.updateMessage("正在处理结果")
loadingController.hide()
```

`timeoutMillis` 为 `null`、`0` 或负数时不启用自动关闭。`hide()` 可重复调用，
隐藏状态下不会重复触发回调。

## 局部 Loading

局部 Loading 放在需要覆盖的父容器中：

```kotlin
View {
    attr {
        height(180f)
        positionRelative()
    }

    // 页面内容

    LoadingOverlay(localController) {
        attr {
            mode = LoadingMode.LOCAL
        }
    }
}
```

组件通过 `absolutePositionAllZero()` 填充实际父容器，因此局部模式不会超出
父容器边界。

## 默认配置

DSL 中的默认超时由 `showDefaults()` 使用：

```kotlin
LoadingOverlay(loadingController) {
    attr {
        defaultMessage = "同步中…"
        defaultTimeoutMillis = 3_000L
    }
}

loadingController.showDefaults()
```

普通 `show()` 保持参数本身的语义，不会自动套用
`defaultTimeoutMillis`。

## 主题和动画

```kotlin
LoadingOverlay(loadingController) {
    attr {
        fullScreenTheme {
            maskColor = Color(0x990F172AL)
            panelColor = Color(0xFF312E81L)
            panelRadius = 18f
            panelPadding = 24f
            indicatorGrayStyle = false
            indicatorScale = 1.5f
            messageColor = Color.WHITE
            messageFontSize = 15f
        }
        animation {
            enabled = true
            durationMillis = 200L
        }
    }
}
```

## 状态行为

当新的 `show()` 替换当前请求时，旧请求收到一次 `REPLACED`，旧计时器同时
取消。每个计时回调还会校验 generation，避免旧回调关闭新请求。

关闭原因包括：

- `MANUAL`
- `TIMEOUT`
- `REPLACED`
- `DESTROYED`

详细状态转换见 [docs/STATE_MACHINE.md](docs/STATE_MACHINE.md)。

## 工程结构

```text
loading-kit/  组件、状态机、Controller 和测试
shared/       Kuikly Demo 页面与 iOS Framework
androidApp/   Android 示例宿主
iosApp/       iOS 示例宿主
docs/         API、架构、构建与验证文档
artifacts/    Android/iOS 运行截图
```

## 构建

项目使用 JDK 17、Gradle 8.7、Kotlin 2.1.21 和 Kuikly
2.23.2-2.1.21。

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export DEVELOPER_DIR=/Applications/Xcode.app/Contents/Developer
export ANDROID_SDK_ROOT="$HOME/Library/Android/sdk"
export ANDROID_HOME="$ANDROID_SDK_ROOT"

./gradlew :loading-kit:allTests
./gradlew :androidApp:assembleDebug
./gradlew :shared:compileTestKotlinIosSimulatorArm64
./gradlew :shared:linkPodDebugFrameworkIosSimulatorArm64
```

iOS 宿主的 CocoaPods 和 Xcode 构建步骤见
[iosApp/README.md](iosApp/README.md)。

## 验证结果

| 项目 | 结果 |
|---|---|
| 状态机与 Controller 测试 | 21 项通过 |
| Android Debug APK | 通过 |
| Android 14 / API 34 ARM64 模拟器 | 运行通过 |
| iOS Simulator ARM64 Framework | 通过 |
| CocoaPods 集成 | 通过 |
| iPhone 17 Pro / iOS 26.0 模拟器 | 运行通过 |

完整命令和截图索引见 [docs/VALIDATION.md](docs/VALIDATION.md)。

## 文档

- [API](docs/API.md)
- [架构](docs/ARCHITECTURE.md)
- [状态机](docs/STATE_MACHINE.md)
- [构建说明](docs/BUILD_BASELINE.md)
- [验证记录](docs/VALIDATION.md)
- [Issue #1480 对照表](docs/ISSUE_1480_CHECKLIST.md)
- [参考资料](docs/REFERENCES.md)

## 已知限制

- 当前 release 提供源码接入，不包含 Maven Central 或 CocoaPods Specs
  包。
- 截图覆盖主要静态状态和超时关闭前后，未提供动画录屏。
- H5 不在本次实现范围内。

## License

本项目代码使用 [Apache License 2.0](LICENSE)。
