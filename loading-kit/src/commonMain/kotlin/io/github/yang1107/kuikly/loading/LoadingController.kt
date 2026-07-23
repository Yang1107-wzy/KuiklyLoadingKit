package io.github.yang1107.kuikly.loading

internal interface LoadingControllerBinding {
    fun defaultRequest(): LoadingRequest
    fun render(snapshot: LoadingSnapshot)
    fun scheduleTimeout(generation: Long, delayMillis: Long)
    fun cancelTimeout(generation: Long)
    fun notifyDismiss(notification: LoadingDismissNotification)
    fun onUnbound()
}

/**
 * Imperative owner of one loading request at a time.
 *
 * Calls are designed for ordered Kuikly UI-thread use. This class does not
 * claim thread safety. Calls made while unbound update the latest state and
 * are delivered when an overlay binds; a positive timeout starts when that
 * state reaches a binding.
 */
public class LoadingController {
    private var machine: LoadingStateMachine = LoadingStateMachine()
    private var binding: LoadingControllerBinding? = null

    /** Whether a live overlay is currently bound. */
    public val isBound: Boolean
        get() = binding != null

    /** Whether the current request state is visible. */
    public val isVisible: Boolean
        get() = machine.snapshot.isVisible

    /**
     * Shows a new request, replacing and dismissing any active request.
     */
    public fun show(
        message: String? = null,
        timeoutMillis: Long? = null,
        blockTouch: Boolean? = null,
    ) {
        show(
            LoadingRequest(
                message = message,
                timeoutMillis = timeoutMillis,
                blockTouch = blockTouch,
            )
        )
    }

    /**
     * Shows an explicit request.
     */
    public fun show(request: LoadingRequest) {
        ensureReusableMachine()
        apply(
            machine.dispatch(
                LoadingEvent.Show(request)
            )
        )
    }

    /**
     * Shows the defaults configured by the currently bound overlay.
     *
     * This deliberately differs from [show]: a `null` timeout passed to
     * [show] always means "no automatic dismissal". Defaults are opt-in and
     * only available while a view is bound.
     *
     * @return `true` when defaults were obtained from a live binding.
     */
    public fun showDefaults(): Boolean {
        val currentBinding = binding ?: return false
        show(currentBinding.defaultRequest())
        return true
    }

    /**
     * Manually hides the active request. Repeated calls are no-ops.
     */
    public fun hide() {
        apply(machine.dispatch(LoadingEvent.Hide))
    }

    /**
     * Updates the active request message.
     *
     * @return `true` when a visible request was updated; otherwise `false`.
     */
    public fun updateMessage(message: String?): Boolean {
        val effects = machine.dispatch(LoadingEvent.UpdateMessage(message))
        apply(effects)
        return effects.any { it is LoadingEffect.Render }
    }

    internal fun bind(newBinding: LoadingControllerBinding) {
        if (binding === newBinding) {
            return
        }

        binding?.onUnbound()
        ensureReusableMachine()
        binding = newBinding
        val snapshot = machine.snapshot
        newBinding.render(snapshot)
        snapshot.request?.effectiveTimeoutMillis?.let { delay ->
            if (snapshot.isVisible) {
                newBinding.scheduleTimeout(snapshot.generation, delay)
            }
        }
    }

    internal fun destroy(destroyedBinding: LoadingControllerBinding) {
        if (binding !== destroyedBinding) {
            return
        }
        apply(machine.dispatch(LoadingEvent.Destroy), destroyedBinding)
        binding = null
        destroyedBinding.onUnbound()
    }

    internal fun timeout(
        sourceBinding: LoadingControllerBinding,
        generation: Long,
    ) {
        if (binding !== sourceBinding) {
            return
        }
        apply(machine.dispatch(LoadingEvent.Timeout(generation)))
    }

    private fun ensureReusableMachine() {
        if (machine.snapshot.isDestroyed) {
            machine = LoadingStateMachine(
                initialGeneration = machine.snapshot.generation,
            )
        }
    }

    private fun apply(
        effects: List<LoadingEffect>,
        target: LoadingControllerBinding? = binding,
    ) {
        if (target == null) {
            return
        }
        effects.forEach { effect ->
            when (effect) {
                is LoadingEffect.Render -> target.render(effect.snapshot)
                is LoadingEffect.ScheduleTimeout -> target.scheduleTimeout(
                    effect.generation,
                    effect.delayMillis,
                )
                is LoadingEffect.CancelTimeout -> target.cancelTimeout(effect.generation)
                is LoadingEffect.NotifyDismiss -> target.notifyDismiss(
                    LoadingDismissNotification(
                        generation = effect.generation,
                        reason = effect.reason,
                    )
                )
            }
        }
    }
}
