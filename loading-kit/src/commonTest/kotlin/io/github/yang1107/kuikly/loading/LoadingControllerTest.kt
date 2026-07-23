package io.github.yang1107.kuikly.loading

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LoadingControllerTest {
    @Test
    fun unboundCallsAreSafeAndLatestStateIsDeliveredOnBind() {
        val controller = LoadingController()

        controller.show(message = "queued", timeoutMillis = 2_000L)
        assertFalse(controller.isBound)
        assertTrue(controller.isVisible)

        val binding = TestBinding(controller)
        controller.bind(binding)

        assertTrue(controller.isBound)
        assertEquals("queued", binding.snapshots.last().request?.message)
        assertEquals(2_000L, binding.scheduler.nextDelayMillis)
    }

    @Test
    fun controllerShowHideAndUpdateHaveStableSemantics() {
        val controller = LoadingController()
        val binding = TestBinding(controller)
        controller.bind(binding)

        assertFalse(controller.updateMessage("ignored"))
        controller.show(message = "before")
        assertTrue(controller.updateMessage("after"))
        assertEquals("after", binding.snapshots.last().request?.message)

        controller.hide()
        controller.hide()

        assertFalse(controller.isVisible)
        assertEquals(
            listOf(LoadingDismissReason.MANUAL),
            binding.dismissals.map { it.reason },
        )
    }

    @Test
    fun manualSchedulerProvesStaleTimeoutCannotCloseReplacement() {
        val controller = LoadingController()
        val binding = TestBinding(controller)
        controller.bind(binding)

        controller.show(message = "A", timeoutMillis = 5_000L)
        binding.scheduler.advanceBy(2_000L)
        controller.show(message = "B", timeoutMillis = 10_000L)
        binding.scheduler.advanceBy(3_000L)

        assertTrue(controller.isVisible)
        assertEquals("B", binding.snapshots.last().request?.message)

        binding.scheduler.advanceBy(7_000L)

        assertFalse(controller.isVisible)
        assertEquals(
            listOf(
                LoadingDismissReason.REPLACED,
                LoadingDismissReason.TIMEOUT,
            ),
            binding.dismissals.map { it.reason },
        )
    }

    @Test
    fun destroyCancelsTimeoutUnbindsControllerAndIgnoresLaterCallbacks() {
        val controller = LoadingController()
        val binding = TestBinding(controller)
        controller.bind(binding)
        controller.show(message = "A", timeoutMillis = 5_000L)

        controller.destroy(binding)
        binding.scheduler.advanceBy(5_000L)

        assertFalse(controller.isBound)
        assertFalse(controller.isVisible)
        assertEquals(
            listOf(LoadingDismissReason.DESTROYED),
            binding.dismissals.map { it.reason },
        )
    }

    @Test
    fun rebindingDetachesOldViewAndOnlyNewViewReceivesUpdates() {
        val controller = LoadingController()
        val oldBinding = TestBinding(controller)
        val newBinding = TestBinding(controller)

        controller.bind(oldBinding)
        controller.show(message = "before", timeoutMillis = 1_000L)
        val oldSnapshotCount = oldBinding.snapshots.size

        controller.bind(newBinding)
        controller.updateMessage("after")
        oldBinding.scheduler.advanceBy(1_000L)

        assertTrue(oldBinding.unbound)
        assertEquals(oldSnapshotCount, oldBinding.snapshots.size)
        assertEquals("after", newBinding.snapshots.last().request?.message)
        assertTrue(controller.isVisible)
    }

    @Test
    fun destroyThenReuseStartsFreshBindingWithoutLeakingOldView() {
        val controller = LoadingController()
        val oldBinding = TestBinding(controller)
        controller.bind(oldBinding)
        controller.show(message = "old")
        controller.destroy(oldBinding)

        controller.show(message = "new")
        val newBinding = TestBinding(controller)
        controller.bind(newBinding)

        assertEquals("new", newBinding.snapshots.last().request?.message)
        assertEquals(2L, newBinding.snapshots.last().generation)
        assertTrue(controller.isVisible)
        assertTrue(oldBinding.unbound)
    }

    @Test
    fun animationConfigurationDoesNotAlterControllerFinalState() {
        listOf(
            LoadingAnimationConfig(enabled = true, durationMillis = 200L),
            LoadingAnimationConfig(enabled = false, durationMillis = 0L),
        ).forEach { config ->
            val controller = LoadingController()
            val binding = TestBinding(controller)
            controller.bind(binding)

            controller.show(message = config.toString())
            controller.hide()

            assertFalse(controller.isVisible)
        }
    }

    @Test
    fun showDefaultsRequiresBindingAndUsesBindingRequest() {
        val controller = LoadingController()
        assertFalse(controller.showDefaults())

        val binding = TestBinding(
            controller = controller,
            defaultLoadingRequest = LoadingRequest(
                message = "Default",
                timeoutMillis = 900L,
                blockTouch = false,
            ),
        )
        controller.bind(binding)

        assertTrue(controller.showDefaults())
        assertTrue(controller.isVisible)
        assertEquals("Default", binding.snapshots.last().request?.message)
        binding.scheduler.advanceBy(900L)
        assertFalse(controller.isVisible)
    }

    private class TestBinding(
        private val controller: LoadingController,
        private val defaultLoadingRequest: LoadingRequest = LoadingRequest(),
    ) : LoadingControllerBinding {
        val snapshots = mutableListOf<LoadingSnapshot>()
        val dismissals = mutableListOf<LoadingDismissNotification>()
        val scheduler = ManualLoadingScheduler()
        private val timeouts = LoadingTimeoutCoordinator(scheduler) { generation ->
            controller.timeout(this, generation)
        }
        var unbound: Boolean = false
            private set

        override fun defaultRequest(): LoadingRequest = defaultLoadingRequest

        override fun render(snapshot: LoadingSnapshot) {
            snapshots += snapshot
        }

        override fun scheduleTimeout(generation: Long, delayMillis: Long) {
            timeouts.schedule(generation, delayMillis)
        }

        override fun cancelTimeout(generation: Long) {
            timeouts.cancel(generation)
        }

        override fun notifyDismiss(notification: LoadingDismissNotification) {
            dismissals += notification
        }

        override fun onUnbound() {
            unbound = true
            timeouts.cancelAll()
        }
    }

    private class ManualLoadingScheduler : LoadingScheduler {
        private data class Entry(
            val dueAtMillis: Long,
            val action: () -> Unit,
            var cancelled: Boolean = false,
        )

        private val entries = mutableListOf<Entry>()
        private var nowMillis: Long = 0L

        val nextDelayMillis: Long?
            get() = entries
                .filterNot { it.cancelled }
                .minByOrNull { it.dueAtMillis }
                ?.let { it.dueAtMillis - nowMillis }

        override fun schedule(
            delayMillis: Long,
            action: () -> Unit,
        ): LoadingScheduledTask {
            val entry = Entry(nowMillis + delayMillis, action)
            entries += entry
            return LoadingScheduledTask {
                entry.cancelled = true
            }
        }

        fun advanceBy(deltaMillis: Long) {
            require(deltaMillis >= 0L)
            val target = nowMillis + deltaMillis
            while (true) {
                val next = entries
                    .filter { !it.cancelled && it.dueAtMillis <= target }
                    .minByOrNull { it.dueAtMillis }
                    ?: break
                nowMillis = next.dueAtMillis
                next.cancelled = true
                next.action()
            }
            nowMillis = target
        }
    }
}
