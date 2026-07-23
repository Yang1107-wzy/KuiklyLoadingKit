package io.github.yang1107.kuikly.loading

/**
 * Determines which parent bounds the overlay occupies.
 *
 * A full-screen overlay must be attached to the page root. A local overlay
 * occupies only the parent container in which it is declared.
 */
public enum class LoadingMode {
    FULL_SCREEN,
    LOCAL,
}

/**
 * The terminal reason emitted for one loading request.
 */
public enum class LoadingDismissReason {
    /** The caller invoked [LoadingController.hide]. */
    MANUAL,

    /** The active positive timeout elapsed. */
    TIMEOUT,

    /** A newer [LoadingController.show] call replaced the request. */
    REPLACED,

    /** The bound overlay was destroyed while this request was visible. */
    DESTROYED,
}

/**
 * Values supplied for one loading request.
 *
 * A `null`, zero, or negative [timeoutMillis] disables automatic dismissal.
 * A `null` [blockTouch] delegates to the overlay's default configuration.
 */
public data class LoadingRequest(
    public val message: String? = null,
    public val timeoutMillis: Long? = null,
    public val blockTouch: Boolean? = null,
) {
    internal val effectiveTimeoutMillis: Long?
        get() = timeoutMillis?.takeIf { it > 0L }
}

/**
 * Immutable state delivered to the bound overlay.
 */
internal data class LoadingSnapshot(
    val isVisible: Boolean,
    val request: LoadingRequest?,
    val generation: Long,
    val isDestroyed: Boolean,
) {
    companion object {
        fun hidden(): LoadingSnapshot = LoadingSnapshot(
            isVisible = false,
            request = null,
            generation = 0L,
            isDestroyed = false,
        )
    }
}

/**
 * A single terminal notification for a request generation.
 */
internal data class LoadingDismissNotification(
    val generation: Long,
    val reason: LoadingDismissReason,
)
