# API

## Core types

### `LoadingMode`

- `FULL_SCREEN`: attach to the page root.
- `LOCAL`: attach inside the bounded parent container.

Both modes fill their actual parent with absolute zero insets. The component
cannot escape its parent layout.

### `LoadingDismissReason`

- `MANUAL`: `hide()` dismissed the active request.
- `TIMEOUT`: the active positive timeout elapsed.
- `REPLACED`: a newer `show()` replaced the request.
- `DESTROYED`: the bound overlay was destroyed while visible.

One request generation emits at most one terminal dismissal.

### `LoadingRequest`

```kotlin
data class LoadingRequest(
    val message: String? = null,
    val timeoutMillis: Long? = null,
    val blockTouch: Boolean? = null,
)
```

- `timeoutMillis == null`, `0`, or a negative value: no automatic dismissal.
- `blockTouch == null`: use the Overlay default.
- An empty effective message leaves only the indicator and panel visible.

## `LoadingController`

```kotlin
class LoadingController {
    val isBound: Boolean
    val isVisible: Boolean

    fun show(
        message: String? = null,
        timeoutMillis: Long? = null,
        blockTouch: Boolean? = null,
    )

    fun show(request: LoadingRequest)
    fun showDefaults(): Boolean
    fun hide()
    fun updateMessage(message: String?): Boolean
}
```

The Controller is designed for ordered Kuikly UI-thread calls. It does not
claim general multi-thread safety.

- `show`: creates a new generation and replaces the current request.
- `showDefaults`: consumes the currently bound Overlay defaults; returns
  `false` if no Overlay is bound.
- `hide`: idempotent.
- `updateMessage`: returns `true` only when a visible request changed.
- Calls to ordinary `show` before binding are retained and rendered at bind.
- Rebinding a visible request preserves its original timeout deadline.

## `LoadingOverlay`

```kotlin
fun ViewContainer<*, *>.LoadingOverlay(
    controller: LoadingController,
    init: LoadingOverlayView.() -> Unit,
)
```

### Attributes

```kotlin
LoadingOverlay(controller) {
    attr {
        mode = LoadingMode.FULL_SCREEN
        defaultMessage = "加载中…"
        defaultTimeoutMillis = 10_000L
        blockTouch = true

        fullScreenTheme { /* LoadingThemeBuilder */ }
        localTheme { /* LoadingThemeBuilder */ }
        theme { /* applies to both */ }

        animation {
            enabled = true
            durationMillis = 200L
        }
    }
}
```

`defaultTimeoutMillis` is deliberately not an implicit fallback for
`show(timeoutMillis = null)`. It is only used by `showDefaults()`.

### Theme builder

- `maskColor`
- `panelColor`
- `panelRadius`
- `panelPadding`
- `indicatorGrayStyle`
- `indicatorScale`
- `messageColor`
- `messageFontSize`

Radius/padding must be non-negative. Scale and font size must be positive.

Kuikly fixes `ActivityIndicator` at approximately `20f × 20f`; scale is applied
with `transform(Scale(...))`. Gray/white style is initialization-time only.

### Events

```kotlin
event {
    onDismiss { reason: LoadingDismissReason ->
        // One terminal callback per request.
    }
}
```

## Touch policy

When visible, the root uses either `touchEnable(true)` or
`touchEnable(false)` from the effective request/default. Hidden Overlays are
transparent and touch-disabled. Cross-platform pass-through remains subject to
the native Kuikly renderer and must be runtime-validated.

## Animation

The root opacity follows the reactive state snapshot. `Animation.easeInOut`
only changes presentation. Animation completion never mutates the authoritative
request state, so rapid show/hide calls remain governed by the state machine.
