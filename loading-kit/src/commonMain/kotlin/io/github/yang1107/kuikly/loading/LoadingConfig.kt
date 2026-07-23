package io.github.yang1107.kuikly.loading

/**
 * Platform-independent animation settings.
 *
 * The state machine never waits for animation callbacks; animations only
 * change how a snapshot is rendered.
 */
public data class LoadingAnimationConfig(
    public val enabled: Boolean = true,
    public val durationMillis: Long = 200L,
) {
    init {
        require(durationMillis >= 0L) {
            "durationMillis must be greater than or equal to zero"
        }
    }
}

