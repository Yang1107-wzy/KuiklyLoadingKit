package io.github.yang1107.kuikly.loading

import kotlin.time.TimeSource

internal fun interface LoadingClock {
    fun nowMillis(): Long
}

internal class MonotonicLoadingClock : LoadingClock {
    private val origin = TimeSource.Monotonic.markNow()

    override fun nowMillis(): Long = origin.elapsedNow().inWholeMilliseconds
}
