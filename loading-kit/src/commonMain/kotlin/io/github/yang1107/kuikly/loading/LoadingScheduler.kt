package io.github.yang1107.kuikly.loading

internal fun interface LoadingScheduledTask {
    fun cancel()
}

internal interface LoadingScheduler {
    fun schedule(
        delayMillis: Long,
        action: () -> Unit,
    ): LoadingScheduledTask
}

/**
 * Owns at most one scheduled timeout and keeps its generation attached.
 */
internal class LoadingTimeoutCoordinator(
    private val scheduler: LoadingScheduler,
    private val onTimeout: (generation: Long) -> Unit,
) {
    private var scheduledGeneration: Long? = null
    private var scheduledTask: LoadingScheduledTask? = null

    fun schedule(
        generation: Long,
        delayMillis: Long,
    ) {
        require(delayMillis > 0L) {
            "Only positive timeouts may be scheduled"
        }
        cancelAll()
        scheduledGeneration = generation
        scheduledTask = scheduler.schedule(delayMillis) {
            if (scheduledGeneration == generation) {
                scheduledGeneration = null
                scheduledTask = null
                onTimeout(generation)
            }
        }
    }

    fun cancel(generation: Long) {
        if (scheduledGeneration == generation) {
            cancelAll()
        }
    }

    fun cancelAll() {
        scheduledTask?.cancel()
        scheduledTask = null
        scheduledGeneration = null
    }
}

