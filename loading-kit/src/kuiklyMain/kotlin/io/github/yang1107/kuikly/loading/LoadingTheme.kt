package io.github.yang1107.kuikly.loading

import com.tencent.kuikly.core.base.Color

/**
 * Visual values used by one loading mode.
 *
 * [maskColor] includes its alpha channel. [indicatorScale] scales Kuikly's
 * fixed 20f × 20f ActivityIndicator instead of assigning an unsupported size.
 */
public data class LoadingTheme(
    val maskColor: Color,
    val panelColor: Color,
    val panelRadius: Float,
    val panelPadding: Float,
    val indicatorGrayStyle: Boolean,
    val indicatorScale: Float,
    val messageColor: Color,
    val messageFontSize: Float,
) {
    public companion object {
        /** High-contrast defaults intended for a page-root overlay. */
        public fun fullScreenDefaults(): LoadingTheme = LoadingTheme(
            maskColor = Color(0x66000000L),
            panelColor = Color.WHITE,
            panelRadius = 12f,
            panelPadding = 20f,
            indicatorGrayStyle = true,
            indicatorScale = 1.2f,
            messageColor = Color.BLACK,
            messageFontSize = 14f,
        )

        /** Slightly lighter defaults intended for a bounded parent container. */
        public fun localDefaults(): LoadingTheme = LoadingTheme(
            maskColor = Color(0x33000000L),
            panelColor = Color(0xF2FFFFFFL),
            panelRadius = 10f,
            panelPadding = 16f,
            indicatorGrayStyle = true,
            indicatorScale = 1f,
            messageColor = Color.BLACK,
            messageFontSize = 13f,
        )
    }
}

/** Mutable DSL builder that produces an immutable [LoadingTheme]. */
public class LoadingThemeBuilder internal constructor(
    theme: LoadingTheme,
) {
    public var maskColor: Color = theme.maskColor
    public var panelColor: Color = theme.panelColor
    public var panelRadius: Float = theme.panelRadius
    public var panelPadding: Float = theme.panelPadding
    public var indicatorGrayStyle: Boolean = theme.indicatorGrayStyle
    public var indicatorScale: Float = theme.indicatorScale
    public var messageColor: Color = theme.messageColor
    public var messageFontSize: Float = theme.messageFontSize

    internal fun build(): LoadingTheme {
        require(panelRadius >= 0f) { "panelRadius must be non-negative" }
        require(panelPadding >= 0f) { "panelPadding must be non-negative" }
        require(indicatorScale > 0f) { "indicatorScale must be positive" }
        require(messageFontSize > 0f) { "messageFontSize must be positive" }
        return LoadingTheme(
            maskColor = maskColor,
            panelColor = panelColor,
            panelRadius = panelRadius,
            panelPadding = panelPadding,
            indicatorGrayStyle = indicatorGrayStyle,
            indicatorScale = indicatorScale,
            messageColor = messageColor,
            messageFontSize = messageFontSize,
        )
    }
}

/** Mutable DSL builder for [LoadingAnimationConfig]. */
public class LoadingAnimationBuilder internal constructor(
    config: LoadingAnimationConfig,
) {
    public var enabled: Boolean = config.enabled
    public var durationMillis: Long = config.durationMillis

    internal fun build(): LoadingAnimationConfig = LoadingAnimationConfig(
        enabled = enabled,
        durationMillis = durationMillis,
    )
}
