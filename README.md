# KuiklyLoadingKit

KuiklyLoadingKit 是为
[Tencent-TDS/KuiklyUI Issue #1480](https://github.com/Tencent-TDS/KuiklyUI/issues/1480)
独立实现的 Kuikly 跨平台加载框组件。项目将纯 Kotlin 请求状态机与
Kuikly UI 分离，提供全屏/局部 Overlay、命令式 Controller、声明式 DSL、
超时失效控制、生命周期清理、主题、触摸策略、淡入淡出动画和交互式 Demo。

本仓库不包含 KuiklyUI Fork，也没有复制其他参与者的加载组件实现。

## 功能

- `FULL_SCREEN`：放在页面根容器，覆盖页面可见区域；
- `LOCAL`：放在目标父容器，只覆盖该父容器布局边界；
- Kuikly `ActivityIndicator` 默认指示器；
- `show`、`hide`、`updateMessage`、`showDefaults`；
- 正超时自动关闭，空值、零值和负值均表示不自动关闭；
- generation/token 与物理 `clearTimeout` 双重保护；
- `MANUAL`、`TIMEOUT`、`REPLACED`、`DESTROYED` 关闭原因；
- 全屏/局部模式独立主题；
- 遮罩、面板、圆角、内边距、文字和指示器缩放配置；
- 可配置触摸拦截及淡入淡出；
- 纯 Kotlin 状态机与 Controller 自动化测试；
- 覆盖 Issue 场景的 `LoadingGalleryPage`。

## 当前验证状态

截至 2026-07-23：

| 范围 | 状态 | 说明 |
|---|---|---|
| JVM 状态测试 | Pass | `:loading-kit:desktopTest` 已执行 |
| iOS Simulator ARM64 组件编译 | Pass | `:loading-kit:compileKotlinIosSimulatorArm64` |
| iOS Simulator ARM64 Demo 编译 | Pass | `:shared:compileKotlinIosSimulatorArm64` |
| iOS 静态 Framework | Pass | `:shared:linkPodDebugFrameworkIosSimulatorArm64` |
| iOS 宿主运行 | Blocked | 本机尚无 CocoaPods，未安装、未运行 |
| Android 构建与运行 | Blocked | 本机尚无 Android SDK/模拟器 |
| 真实截图/GIF | Blocked | 只会在真实宿主运行后采集 |

完整命令、环境和证据边界见
[docs/VALIDATION.md](docs/VALIDATION.md)。

## 最小用法

先创建 Controller：

```kotlin
private val loadingController = LoadingController()
```

将全屏 Overlay 放在页面根容器的最后：

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

显示、更新与关闭：

```kotlin
loadingController.show(
    message = "正在请求数据",
    timeoutMillis = 10_000L,
    blockTouch = true,
)

loadingController.updateMessage("正在处理结果")
loadingController.hide()
```

`hide()` 是幂等操作；隐藏状态重复调用不会重复触发关闭事件。

## 默认值 DSL

`show()` 中的 `timeoutMillis = null` 始终表示“不自动关闭”。为了不混淆
这个规则，DSL 中的 `defaultTimeoutMillis` 只由显式的 `showDefaults()` 使用：

```kotlin
LoadingOverlay(loadingController) {
    attr {
        defaultMessage = "同步中…"
        defaultTimeoutMillis = 3_000L
        blockTouch = true
    }
}

loadingController.showDefaults()
```

Controller 未绑定时 `showDefaults()` 返回 `false`；普通 `show()` 在未绑定时
仍会安全保存最新状态，并在 Overlay 绑定后渲染。

## 局部模式

局部 Overlay 必须是目标容器的子元素，推荐作为最后一个子元素：

```kotlin
View {
    attr {
        height(180f)
        positionRelative()
    }

    // Local content...

    LoadingOverlay(localController) {
        attr {
            mode = LoadingMode.LOCAL
        }
    }
}
```

组件使用 `absolutePositionAllZero()` 填充父容器，不能超越父容器的真实布局
边界。`FULL_SCREEN` 和 `LOCAL` 的差异由声明位置及各自主题共同表达。

## 主题与动画

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
        localTheme {
            maskColor = Color(0x2200AA55L)
        }
        animation {
            enabled = true
            durationMillis = 200L
        }
    }
}
```

Kuikly 的 `ActivityIndicator` 跨端固定约为 `20f × 20f`，本组件使用
`transform(Scale(...))` 调整显示比例。`indicatorGrayStyle` 是初始化配置，
不会承诺运行时动态切换。

## 超时与替换语义

```text
show(A, timeout=5s)
2s 后 show(B, timeout=10s)
```

- A 立即收到一次 `REPLACED`；
- A 的原 Timeout 会被取消，并由 generation 再次校验；
- A 的旧回调不能关闭 B；
- B 从第二次 `show()` 起重新计时；
- 每个请求最多产生一次最终关闭事件。

## 生命周期

Overlay 在 `viewDidLoad` 绑定 Controller，在 `viewWillUnload` /
`viewDestroyed` 解除绑定、取消计时器并使旧回调失效。若销毁时请求可见，
只产生一次 `DESTROYED`。旧 View 被替换绑定后不再接收新状态。

## 触摸行为

- `blockTouch = true`：可见时启用 Overlay 的命中，拦截其覆盖区域；
- `blockTouch = false`：通过 Kuikly `touchEnable(false)` 尝试允许底层交互；
- 隐藏时 Overlay 始终透明且禁用触摸。

真正的穿透行为必须分别在 Android/iOS 宿主验证。当前尚未完成运行验证，
因此不对两个平台写出未经证实的结论。

## 工程结构

```text
loading-kit/  可复用组件、纯状态机、Controller 和测试
shared/       LoadingGalleryPage 与 KMP/CocoaPods Framework
androidApp/   最小 Android Kuikly 宿主
iosApp/       最小 iOS Kuikly 宿主
docs/         API、架构、状态机、决策和验证记录
artifacts/    只保存真实运行证据
```

## 本地构建

要求 JDK 17。以下命令只为当前 Shell 指定 Java，不修改全局默认：

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export DEVELOPER_DIR=/Applications/Xcode.app/Contents/Developer

./gradlew :loading-kit:desktopTest
./gradlew :loading-kit:allTests
./gradlew :shared:compileKotlinIosSimulatorArm64
./gradlew :shared:linkPodDebugFrameworkIosSimulatorArm64
```

Android SDK 存在时，项目会自动启用 Android target 和 `androidApp`：

```bash
./gradlew :androidApp:assembleDebug
```

iOS 宿主需要 CocoaPods：

```bash
./gradlew :shared:generateDummyFramework
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

## Demo

`LoadingGalleryPage` 提供：

- FullScreenLoadingDemo
- LocalLoadingDemo
- TimeoutLoadingDemo
- RepeatedShowDemo
- UpdateMessageDemo
- CustomThemeDemo
- TouchBlockingDemo
- LifecycleCleanupDemo

Android/iOS 宿主均以 `LoadingGalleryPage` 为入口。真实截图尚未生成，证据目录
中的说明文件明确记录采集条件。

## 已知限制

- 尚未在 Android 或 iOS Simulator 中完成运行与触摸行为验证；
- Android target 在没有 SDK 的机器上会被构建配置有意关闭；
- CocoaPods `pod install` 尚未执行；
- 未发布 Maven/CocoaPods 远程制品，目前通过项目依赖使用；
- H5 不在 Issue #1480 的 P0 范围。

## 文档

- [API](docs/API.md)
- [Architecture](docs/ARCHITECTURE.md)
- [State machine](docs/STATE_MACHINE.md)
- [Validation](docs/VALIDATION.md)
- [Issue #1480 checklist](docs/ISSUE_1480_CHECKLIST.md)
- [Originality and references](docs/ORIGINALITY_AND_REFERENCES.md)

## License

本项目自有代码使用 [Apache License 2.0](LICENSE)。Kuikly 及其他依赖继续
受各自许可证约束。
