package io.github.yang1107.kuikly.loading.demo

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.timer.clearTimeout
import com.tencent.kuikly.core.timer.setTimeout
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import io.github.yang1107.kuikly.loading.LoadingController
import io.github.yang1107.kuikly.loading.LoadingDismissReason
import io.github.yang1107.kuikly.loading.LoadingMode
import io.github.yang1107.kuikly.loading.LoadingOverlay

/**
 * Interactive acceptance gallery for Tencent-TDS/KuiklyUI issue #1480.
 */
@Page("LoadingGalleryPage", supportInLocal = true)
public class LoadingGalleryPage : Pager() {
    private val fullScreenController = LoadingController()
    private val customThemeController = LoadingController()
    private val noAnimationController = LoadingController()
    private val localController = LoadingController()
    private val touchPassThroughController = LoadingController()
    private val lifecycleController = LoadingController()

    private var latestEvent: String by observable("尚未触发关闭事件")
    private var underlayTapCount: Int by observable(0)
    private var lifecycleOverlayMounted: Boolean by observable(true)
    private var acceptanceScenarioScheduled: Boolean = false
    private var acceptanceScenarioTimer: String? = null

    override fun body(): ViewBuilder {
        val context = this
        val acceptanceScenario = AcceptanceScenario.parse(
            pagerData.params.optString(ACCEPTANCE_SCENARIO_PARAM)
        )
        scheduleAcceptanceScenario(acceptanceScenario)
        return {
            View {
                attr {
                    flex(1f)
                    backgroundColor(Color(0xFFF4F6F8L))
                }

                View {
                    attr {
                        padding(
                            top = context.pagerData.statusBarHeight + 14f,
                            left = 18f,
                            bottom = 14f,
                            right = 18f,
                        )
                        backgroundColor(Color(0xFF111827L))
                    }
                    Text {
                        attr {
                            text("KuiklyLoadingKit")
                            color(Color.WHITE)
                            fontSize(22f)
                            fontWeightSemiBold()
                        }
                    }
                    Text {
                        attr {
                            text("Issue #1480 · Interactive acceptance gallery")
                            color(Color(0xFFCBD5E1L))
                            fontSize(12f)
                            marginTop(5f)
                        }
                    }
                    Text {
                        attr {
                            text("Last event: ${context.latestEvent}")
                            color(Color(0xFF93C5FDL))
                            fontSize(12f)
                            marginTop(8f)
                        }
                    }
                }

                Scroller {
                    attr {
                        flex(1f)
                        flexDirectionColumn()
                        padding(top = 16f, left = 16f, bottom = 36f, right = 16f)
                    }

                    DemoSection(
                        title = "FullScreenLoadingDemo",
                        description = "Page-root overlay; manual hide and 3-second timeout.",
                    ) {
                        DemoButton("Show full screen") {
                            context.fullScreenController.show(
                                message = "正在加载页面数据…",
                                blockTouch = true,
                            )
                            context.setTimeout(1_600) {
                                context.fullScreenController.hide()
                            }
                        }
                        DemoButton("3-second timeout") {
                            context.fullScreenController.show(
                                message = "3 秒后自动关闭",
                                timeoutMillis = 3_000L,
                                blockTouch = true,
                            )
                        }
                    }

                    DemoSection(
                        title = "RepeatedShowDemo",
                        description = "A is replaced by B; A's stale timeout cannot close B.",
                    ) {
                        DemoButton("Run A → B") {
                            context.fullScreenController.show(
                                message = "请求 A（5 秒超时）",
                                timeoutMillis = 5_000L,
                            )
                            context.setTimeout(1_000) {
                                context.fullScreenController.show(
                                    message = "请求 B（3 秒超时）",
                                    timeoutMillis = 3_000L,
                                )
                            }
                        }
                    }

                    DemoSection(
                        title = "UpdateMessageDemo",
                        description = "The visible request changes text without creating a new request.",
                    ) {
                        DemoButton("Show then update") {
                            context.fullScreenController.show(
                                message = "阶段 1/2：准备中",
                                timeoutMillis = 4_000L,
                            )
                            context.setTimeout(1_200) {
                                context.fullScreenController.updateMessage("阶段 2/2：即将完成")
                            }
                        }
                    }

                    DemoSection(
                        title = "CustomThemeDemo",
                        description = "Independent full-screen palette and ActivityIndicator scale.",
                    ) {
                        DemoButton("Show custom theme") {
                            context.customThemeController.show(
                                message = "使用自定义主题",
                                timeoutMillis = 3_000L,
                            )
                        }
                        DemoButton("Show without animation") {
                            context.noAnimationController.show(
                                message = "动画已关闭",
                                timeoutMillis = 1_800L,
                            )
                        }
                    }

                    DemoSection(
                        title = "LocalLoadingDemo",
                        description = "The overlay is bounded by this parent card.",
                    ) {
                        View {
                            attr {
                                height(150f)
                                positionRelative()
                                backgroundColor(Color(0xFFEFF6FFL))
                                borderRadius(10f)
                                border(
                                    Border(
                                        lineWidth = 1f,
                                        lineStyle = BorderStyle.SOLID,
                                        color = Color(0xFFBFDBFEL),
                                    )
                                )
                                padding(14f)
                            }
                            Text {
                                attr {
                                    text("局部内容仍由父容器限定布局边界")
                                    color(Color(0xFF1E3A8AL))
                                    fontSize(13f)
                                }
                            }
                            DemoButton("Show local overlay") {
                                context.localController.show(
                                    message = "卡片处理中…",
                                    timeoutMillis = 2_500L,
                                )
                            }
                            LoadingOverlay(context.localController) {
                                attr {
                                    mode = LoadingMode.LOCAL
                                    defaultMessage = "局部加载"
                                }
                                event {
                                    onDismiss(context::recordDismiss)
                                }
                            }
                        }
                    }

                    DemoSection(
                        title = "TouchBlockingDemo",
                        description = "blockTouch=false uses Kuikly touchEnable(false) for best-effort pass-through.",
                    ) {
                        View {
                            attr {
                                height(160f)
                                positionRelative()
                                backgroundColor(Color(0xFFF0FDF4L))
                                borderRadius(10f)
                                padding(14f)
                            }
                            Text {
                                attr {
                                    text("底层点击计数：${context.underlayTapCount}")
                                    color(Color(0xFF14532DL))
                                    fontSize(13f)
                                }
                            }
                            DemoButton("Tap underlay button") {
                                context.underlayTapCount += 1
                            }
                            DemoButton("Show pass-through overlay") {
                                context.touchPassThroughController.show(
                                    message = "尝试继续点击底层按钮",
                                    timeoutMillis = 4_000L,
                                    blockTouch = false,
                                )
                            }
                            LoadingOverlay(context.touchPassThroughController) {
                                attr {
                                    mode = LoadingMode.LOCAL
                                    blockTouch = false
                                    localTheme {
                                        maskColor = Color(0x2200AA55L)
                                        panelColor = Color(0xF2FFFFFFL)
                                    }
                                }
                                event {
                                    onDismiss(context::recordDismiss)
                                }
                            }
                        }
                    }

                    DemoSection(
                        title = "LifecycleCleanupDemo",
                        description = "Unmounting a visible overlay emits DESTROYED once and invalidates its timeout.",
                    ) {
                        DemoButton("Mount overlay") {
                            context.lifecycleOverlayMounted = true
                        }
                        DemoButton("Show lifecycle overlay") {
                            context.lifecycleOverlayMounted = true
                            context.setTimeout(1) {
                                context.lifecycleController.show(
                                    message = "卸载我以验证清理",
                                    timeoutMillis = 5_000L,
                                )
                            }
                        }
                        DemoButton("Unmount overlay") {
                            context.lifecycleOverlayMounted = false
                        }
                        View {
                            attr {
                                height(90f)
                                positionRelative()
                                marginTop(10f)
                                borderRadius(10f)
                                backgroundColor(Color(0xFFFFF7EDL))
                                allCenter()
                            }
                            Text {
                                attr {
                                    text(
                                        if (context.lifecycleOverlayMounted) {
                                            "Lifecycle overlay mounted"
                                        } else {
                                            "Lifecycle overlay unmounted"
                                        }
                                    )
                                    color(Color(0xFF9A3412L))
                                    fontSize(13f)
                                }
                            }
                            vif({ context.lifecycleOverlayMounted }) {
                                LoadingOverlay(context.lifecycleController) {
                                    attr {
                                        mode = LoadingMode.LOCAL
                                    }
                                    event {
                                        onDismiss(context::recordDismiss)
                                    }
                                }
                            }
                        }
                    }
                }

                if (acceptanceScenario == AcceptanceScenario.LOCAL) {
                    View {
                        attr {
                            positionAbsolute()
                            left(24f)
                            right(24f)
                            top(context.pagerData.statusBarHeight + 185f)
                            height(220f)
                            padding(18f)
                            borderRadius(16f)
                            backgroundColor(Color(0xFFEFF6FFL))
                            border(
                                Border(
                                    lineWidth = 2f,
                                    lineStyle = BorderStyle.SOLID,
                                    color = Color(0xFF60A5FAL),
                                )
                            )
                        }
                        Text {
                            attr {
                                text("Local acceptance stage")
                                color(Color(0xFF1E3A8AL))
                                fontSize(18f)
                                fontWeightSemiBold()
                            }
                        }
                        Text {
                            attr {
                                text("遮罩和指示器严格限制在此卡片内")
                                color(Color(0xFF1D4ED8L))
                                fontSize(13f)
                                marginTop(8f)
                            }
                        }
                        LoadingOverlay(context.localController) {
                            attr {
                                mode = LoadingMode.LOCAL
                                defaultMessage = "局部加载"
                            }
                            event {
                                onDismiss(context::recordDismiss)
                            }
                        }
                    }
                }

                LoadingOverlay(context.fullScreenController) {
                    attr {
                        mode = LoadingMode.FULL_SCREEN
                        defaultMessage = "加载中…"
                        blockTouch = true
                    }
                    event {
                        onDismiss(context::recordDismiss)
                    }
                }

                LoadingOverlay(context.customThemeController) {
                    attr {
                        mode = LoadingMode.FULL_SCREEN
                        fullScreenTheme {
                            maskColor = Color(0xB30F172AL)
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
                            durationMillis = 320L
                        }
                    }
                    event {
                        onDismiss(context::recordDismiss)
                    }
                }

                LoadingOverlay(context.noAnimationController) {
                    attr {
                        mode = LoadingMode.FULL_SCREEN
                        animation {
                            enabled = false
                            durationMillis = 0L
                        }
                    }
                    event {
                        onDismiss(context::recordDismiss)
                    }
                }
            }
        }
    }

    private fun recordDismiss(reason: LoadingDismissReason) {
        latestEvent = reason.name
    }

    override fun pageWillDestroy() {
        acceptanceScenarioTimer?.let(::clearTimeout)
        acceptanceScenarioTimer = null
        super.pageWillDestroy()
    }

    private fun scheduleAcceptanceScenario(scenario: AcceptanceScenario) {
        if (
            acceptanceScenarioScheduled ||
            scenario == AcceptanceScenario.GALLERY
        ) {
            return
        }
        acceptanceScenarioScheduled = true
        acceptanceScenarioTimer = setTimeout(700) {
            acceptanceScenarioTimer = null
            when (scenario) {
                AcceptanceScenario.FULL_SCREEN -> {
                    fullScreenController.show(
                        message = "全屏加载验收场景",
                        timeoutMillis = ACCEPTANCE_TIMEOUT_MILLIS,
                        blockTouch = true,
                    )
                }
                AcceptanceScenario.TIMEOUT -> {
                    fullScreenController.show(
                        message = "自动关闭验收：8 秒倒计时",
                        timeoutMillis = ACCEPTANCE_TIMEOUT_MILLIS,
                        blockTouch = true,
                    )
                }
                AcceptanceScenario.CUSTOM_THEME -> {
                    customThemeController.show(
                        message = "自定义主题验收场景",
                        timeoutMillis = ACCEPTANCE_TIMEOUT_MILLIS,
                    )
                }
                AcceptanceScenario.LOCAL -> {
                    localController.show(
                        message = "卡片内局部加载",
                        timeoutMillis = ACCEPTANCE_TIMEOUT_MILLIS,
                    )
                }
                AcceptanceScenario.GALLERY -> Unit
            }
        }
    }

    private companion object {
        const val ACCEPTANCE_SCENARIO_PARAM: String = "acceptanceScenario"
        const val ACCEPTANCE_TIMEOUT_MILLIS: Long = 8_000L
    }
}

private fun ViewContainer<*, *>.DemoSection(
    title: String,
    description: String,
    content: ViewContainer<*, *>.() -> Unit,
) {
    View {
        attr {
            backgroundColor(Color.WHITE)
            borderRadius(12f)
            padding(14f)
            marginBottom(14f)
        }
        Text {
            attr {
                text(title)
                color(Color(0xFF111827L))
                fontSize(16f)
                fontWeightSemiBold()
            }
        }
        Text {
            attr {
                text(description)
                color(Color(0xFF6B7280L))
                fontSize(12f)
                marginTop(5f)
                marginBottom(10f)
            }
        }
        content()
    }
}

private fun ViewContainer<*, *>.DemoButton(
    title: String,
    onClick: () -> Unit,
) {
    View {
        attr {
            height(38f)
            backgroundColor(Color(0xFF2563EBL))
            borderRadius(8f)
            allCenter()
            marginTop(8f)
            touchEnable(true)
        }
        event {
            click {
                onClick()
            }
        }
        Text {
            attr {
                text(title)
                color(Color.WHITE)
                fontSize(13f)
                fontWeightMedium()
            }
        }
    }
}
