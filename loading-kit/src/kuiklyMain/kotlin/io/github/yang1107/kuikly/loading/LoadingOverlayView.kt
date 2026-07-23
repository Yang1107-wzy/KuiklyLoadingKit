package io.github.yang1107.kuikly.loading

import com.tencent.kuikly.core.base.Animation
import com.tencent.kuikly.core.base.ComposeView
import com.tencent.kuikly.core.base.Scale
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.ActivityIndicator
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View

/**
 * Kuikly loading overlay bound to a [LoadingController].
 *
 * The view always stays in the hierarchy so fade-out can complete. While
 * hidden it is fully transparent and touch-disabled; the state machine, not an
 * animation completion callback, remains authoritative.
 */
public class LoadingOverlayView internal constructor(
    private val controller: LoadingController,
) : ComposeView<LoadingOverlayAttr, LoadingOverlayEvent>() {
    private var snapshot: LoadingSnapshot by observable(LoadingSnapshot.hidden())
    private lateinit var timeouts: LoadingTimeoutCoordinator
    private var controllerDetached: Boolean = false
    private val controllerBinding = object : LoadingControllerBinding {
        override fun defaultRequest(): LoadingRequest = attr.defaultRequest()

        override fun render(snapshot: LoadingSnapshot) {
            this@LoadingOverlayView.snapshot = snapshot
        }

        override fun scheduleTimeout(generation: Long, delayMillis: Long) {
            timeouts.schedule(generation, delayMillis)
        }

        override fun cancelTimeout(generation: Long) {
            timeouts.cancel(generation)
        }

        override fun notifyDismiss(notification: LoadingDismissNotification) {
            emit(LoadingOverlayEvent.DISMISS_EVENT, notification.reason)
        }

        override fun onUnbound() {
            timeouts.cancelAll()
            this@LoadingOverlayView.snapshot = this@LoadingOverlayView.snapshot.copy(
                isVisible = false,
                request = null,
            )
        }
    }

    override fun created() {
        super.created()
        timeouts = LoadingTimeoutCoordinator(
            scheduler = KuiklyLoadingScheduler(this),
        ) { generation ->
            controller.timeout(controllerBinding, generation)
        }
    }

    override fun viewDidLoad() {
        super.viewDidLoad()
        controllerDetached = false
        controller.bind(controllerBinding)
    }

    override fun viewWillUnload() {
        detachController()
        super.viewWillUnload()
    }

    override fun viewDestroyed() {
        detachController()
        super.viewDestroyed()
    }

    override fun createAttr(): LoadingOverlayAttr = LoadingOverlayAttr()

    override fun createEvent(): LoadingOverlayEvent = LoadingOverlayEvent()

    override fun body(): ViewBuilder {
        val context = this
        return {
            attr {
                val visible = context.snapshot.isVisible
                val request = context.snapshot.request
                val shouldBlockTouch = visible && (request?.blockTouch ?: context.attr.blockTouch)
                val animation = context.attr.animationConfig

                absolutePositionAllZero()
                zIndex(1_000)
                allCenter()
                backgroundColor(context.attr.selectedTheme().maskColor)
                touchEnable(shouldBlockTouch)
                if (animation.enabled && animation.durationMillis > 0L) {
                    animate(
                        Animation.easeInOut(animation.durationMillis / 1_000f),
                        value = visible,
                    )
                }
                opacity(if (visible) 1f else 0f)
            }

            View {
                attr {
                    val theme = context.attr.selectedTheme()
                    backgroundColor(theme.panelColor)
                    borderRadius(theme.panelRadius)
                    padding(theme.panelPadding)
                    allCenter()
                }

                ActivityIndicator {
                    attr {
                        val theme = context.attr.selectedTheme()
                        isGrayStyle(theme.indicatorGrayStyle)
                        transform(Scale(theme.indicatorScale, theme.indicatorScale))
                    }
                }

                Text {
                    attr {
                        val theme = context.attr.selectedTheme()
                        val message = context.snapshot.request?.message
                            ?: context.attr.defaultMessage
                            ?: ""
                        text(message)
                        color(theme.messageColor)
                        fontSize(theme.messageFontSize)
                        marginTop(if (message.isEmpty()) 0f else 12f)
                    }
                }
            }
        }
    }

    private fun detachController() {
        if (controllerDetached) {
            return
        }
        controllerDetached = true
        controller.destroy(controllerBinding)
    }
}

/**
 * Adds a loading overlay to this parent.
 *
 * Put a FULL_SCREEN overlay under the page root. Put a LOCAL overlay as the
 * final child of the bounded container it should cover.
 */
public fun ViewContainer<*, *>.LoadingOverlay(
    controller: LoadingController,
    init: LoadingOverlayView.() -> Unit,
) {
    addChild(LoadingOverlayView(controller), init)
}
