package io.github.yang1107.kuikly.loading

internal sealed interface LoadingMachineState {
    data object Hidden : LoadingMachineState

    data class Visible(
        val generation: Long,
        val request: LoadingRequest,
    ) : LoadingMachineState

    data object Destroyed : LoadingMachineState
}

internal sealed interface LoadingEvent {
    data class Show(val request: LoadingRequest) : LoadingEvent
    data object Hide : LoadingEvent
    data class UpdateMessage(val message: String?) : LoadingEvent
    data class Timeout(val generation: Long) : LoadingEvent
    data object Destroy : LoadingEvent
}

internal sealed interface LoadingEffect {
    data class Render(val snapshot: LoadingSnapshot) : LoadingEffect
    data class ScheduleTimeout(
        val generation: Long,
        val delayMillis: Long,
    ) : LoadingEffect

    data class CancelTimeout(val generation: Long) : LoadingEffect
    data class NotifyDismiss(
        val generation: Long,
        val reason: LoadingDismissReason,
    ) : LoadingEffect
}

/**
 * Pure deterministic request state machine.
 *
 * It produces effects but never calls platform APIs directly. Generation
 * checks are authoritative even when a platform cannot physically cancel a
 * timer callback.
 */
internal class LoadingStateMachine(
    initialGeneration: Long = 0L,
) {
    private var lastGeneration: Long = initialGeneration

    var state: LoadingMachineState = LoadingMachineState.Hidden
        private set

    val snapshot: LoadingSnapshot
        get() = when (val current = state) {
            LoadingMachineState.Hidden -> LoadingSnapshot(
                isVisible = false,
                request = null,
                generation = lastGeneration,
                isDestroyed = false,
            )

            is LoadingMachineState.Visible -> LoadingSnapshot(
                isVisible = true,
                request = current.request,
                generation = current.generation,
                isDestroyed = false,
            )

            LoadingMachineState.Destroyed -> LoadingSnapshot(
                isVisible = false,
                request = null,
                generation = lastGeneration,
                isDestroyed = true,
            )
        }

    fun dispatch(event: LoadingEvent): List<LoadingEffect> {
        if (state === LoadingMachineState.Destroyed) {
            return emptyList()
        }

        return when (event) {
            is LoadingEvent.Show -> show(event.request)
            LoadingEvent.Hide -> dismiss(LoadingDismissReason.MANUAL)
            is LoadingEvent.UpdateMessage -> updateMessage(event.message)
            is LoadingEvent.Timeout -> timeout(event.generation)
            LoadingEvent.Destroy -> destroy()
        }
    }

    private fun show(request: LoadingRequest): List<LoadingEffect> {
        val effects = mutableListOf<LoadingEffect>()
        val current = state
        if (current is LoadingMachineState.Visible) {
            effects += LoadingEffect.CancelTimeout(current.generation)
            effects += LoadingEffect.NotifyDismiss(
                generation = current.generation,
                reason = LoadingDismissReason.REPLACED,
            )
        }

        lastGeneration += 1L
        state = LoadingMachineState.Visible(
            generation = lastGeneration,
            request = request,
        )
        effects += LoadingEffect.Render(snapshot)
        request.effectiveTimeoutMillis?.let { delay ->
            effects += LoadingEffect.ScheduleTimeout(lastGeneration, delay)
        }
        return effects
    }

    private fun dismiss(reason: LoadingDismissReason): List<LoadingEffect> {
        val current = state as? LoadingMachineState.Visible ?: return emptyList()
        state = LoadingMachineState.Hidden
        return listOf(
            LoadingEffect.CancelTimeout(current.generation),
            LoadingEffect.Render(snapshot),
            LoadingEffect.NotifyDismiss(current.generation, reason),
        )
    }

    private fun timeout(generation: Long): List<LoadingEffect> {
        val current = state as? LoadingMachineState.Visible ?: return emptyList()
        if (current.generation != generation) {
            return emptyList()
        }
        return dismiss(LoadingDismissReason.TIMEOUT)
    }

    private fun updateMessage(message: String?): List<LoadingEffect> {
        val current = state as? LoadingMachineState.Visible ?: return emptyList()
        state = current.copy(
            request = current.request.copy(message = message),
        )
        return listOf(LoadingEffect.Render(snapshot))
    }

    private fun destroy(): List<LoadingEffect> {
        val current = state
        state = LoadingMachineState.Destroyed
        return if (current is LoadingMachineState.Visible) {
            listOf(
                LoadingEffect.CancelTimeout(current.generation),
                LoadingEffect.Render(snapshot),
                LoadingEffect.NotifyDismiss(
                    current.generation,
                    LoadingDismissReason.DESTROYED,
                ),
            )
        } else {
            listOf(LoadingEffect.Render(snapshot))
        }
    }
}
