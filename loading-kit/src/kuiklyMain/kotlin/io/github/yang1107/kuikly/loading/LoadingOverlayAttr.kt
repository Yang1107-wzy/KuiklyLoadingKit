package io.github.yang1107.kuikly.loading

import com.tencent.kuikly.core.base.ComposeAttr

/**
 * Declarative defaults and appearance for [LoadingOverlayView].
 *
 * [defaultTimeoutMillis] is only used by [LoadingController.showDefaults].
 * Passing `timeoutMillis = null` to [LoadingController.show] always means no
 * automatic dismissal.
 */
public class LoadingOverlayAttr : ComposeAttr() {
    public var mode: LoadingMode = LoadingMode.FULL_SCREEN
    public var defaultMessage: String? = null
    public var defaultTimeoutMillis: Long? = null
    public var blockTouch: Boolean = true
    public var fullScreenTheme: LoadingTheme = LoadingTheme.fullScreenDefaults()
    public var localTheme: LoadingTheme = LoadingTheme.localDefaults()
    public var animationConfig: LoadingAnimationConfig = LoadingAnimationConfig()

    /** Applies the same customization block to both mode-specific themes. */
    public fun theme(block: LoadingThemeBuilder.() -> Unit) {
        fullScreenTheme(block)
        localTheme(block)
    }

    /** Customizes only [LoadingMode.FULL_SCREEN]. */
    public fun fullScreenTheme(block: LoadingThemeBuilder.() -> Unit) {
        fullScreenTheme = LoadingThemeBuilder(fullScreenTheme).apply(block).build()
    }

    /** Customizes only [LoadingMode.LOCAL]. */
    public fun localTheme(block: LoadingThemeBuilder.() -> Unit) {
        localTheme = LoadingThemeBuilder(localTheme).apply(block).build()
    }

    /** Configures fade animation without changing state-machine semantics. */
    public fun animation(block: LoadingAnimationBuilder.() -> Unit) {
        animationConfig = LoadingAnimationBuilder(animationConfig).apply(block).build()
    }

    internal fun selectedTheme(): LoadingTheme = when (mode) {
        LoadingMode.FULL_SCREEN -> fullScreenTheme
        LoadingMode.LOCAL -> localTheme
    }

    internal fun defaultRequest(): LoadingRequest = LoadingRequest(
        message = defaultMessage,
        timeoutMillis = defaultTimeoutMillis,
        blockTouch = blockTouch,
    )
}
