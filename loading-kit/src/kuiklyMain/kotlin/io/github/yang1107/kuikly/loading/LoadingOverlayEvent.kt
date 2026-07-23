package io.github.yang1107.kuikly.loading

import com.tencent.kuikly.core.base.ComposeEvent

/** Events emitted by [LoadingOverlayView]. */
public class LoadingOverlayEvent : ComposeEvent() {
    /**
     * Registers a callback invoked once for each request's terminal dismissal.
     */
    public fun onDismiss(handler: (LoadingDismissReason) -> Unit) {
        registerEvent(DISMISS_EVENT) { payload ->
            val reason = payload as? LoadingDismissReason ?: return@registerEvent
            handler(reason)
        }
    }

    internal companion object {
        const val DISMISS_EVENT: String = "kuiklyLoadingDismiss"
    }
}
