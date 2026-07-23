package io.github.yang1107.kuikly.loading

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LoadingStateMachineTest {
    @Test
    fun initialStateIsHidden() {
        val machine = LoadingStateMachine()

        assertFalse(machine.snapshot.isVisible)
        assertNull(machine.snapshot.request)
        assertEquals(0L, machine.snapshot.generation)
    }

    @Test
    fun showCreatesVisibleRequestAndSchedulesPositiveTimeout() {
        val machine = LoadingStateMachine()

        val effects = machine.dispatch(
            LoadingEvent.Show(
                LoadingRequest(message = "A", timeoutMillis = 5_000L)
            )
        )

        assertTrue(machine.snapshot.isVisible)
        assertEquals("A", machine.snapshot.request?.message)
        assertEquals(1L, machine.snapshot.generation)
        assertEquals(
            listOf(
                LoadingEffect.Render(machine.snapshot),
                LoadingEffect.ScheduleTimeout(1L, 5_000L),
            ),
            effects,
        )
    }

    @Test
    fun hideDismissesVisibleRequestOnceAndIsIdempotent() {
        val machine = LoadingStateMachine()
        machine.dispatch(LoadingEvent.Show(LoadingRequest(message = "A")))

        val firstHide = machine.dispatch(LoadingEvent.Hide)
        val secondHide = machine.dispatch(LoadingEvent.Hide)

        assertFalse(machine.snapshot.isVisible)
        assertEquals(
            listOf(
                LoadingEffect.CancelTimeout(1L),
                LoadingEffect.Render(machine.snapshot),
                LoadingEffect.NotifyDismiss(1L, LoadingDismissReason.MANUAL),
            ),
            firstHide,
        )
        assertTrue(secondHide.isEmpty())
    }

    @Test
    fun newerShowReplacesOldRequestAndInvalidatesOldGeneration() {
        val machine = LoadingStateMachine()
        machine.dispatch(
            LoadingEvent.Show(
                LoadingRequest(message = "A", timeoutMillis = 5_000L)
            )
        )

        val effects = machine.dispatch(
            LoadingEvent.Show(
                LoadingRequest(message = "B", timeoutMillis = 10_000L)
            )
        )

        assertEquals("B", machine.snapshot.request?.message)
        assertEquals(2L, machine.snapshot.generation)
        assertEquals(
            listOf(
                LoadingEffect.CancelTimeout(1L),
                LoadingEffect.NotifyDismiss(1L, LoadingDismissReason.REPLACED),
                LoadingEffect.Render(machine.snapshot),
                LoadingEffect.ScheduleTimeout(2L, 10_000L),
            ),
            effects,
        )
    }

    @Test
    fun staleTimeoutCannotCloseReplacement() {
        val machine = LoadingStateMachine()
        machine.dispatch(LoadingEvent.Show(LoadingRequest(message = "A", timeoutMillis = 5_000L)))
        machine.dispatch(LoadingEvent.Show(LoadingRequest(message = "B", timeoutMillis = 10_000L)))

        val staleEffects = machine.dispatch(LoadingEvent.Timeout(generation = 1L))

        assertTrue(staleEffects.isEmpty())
        assertTrue(machine.snapshot.isVisible)
        assertEquals("B", machine.snapshot.request?.message)
    }

    @Test
    fun currentTimeoutClosesCurrentRequest() {
        val machine = LoadingStateMachine()
        machine.dispatch(LoadingEvent.Show(LoadingRequest(timeoutMillis = 3_000L)))

        val effects = machine.dispatch(LoadingEvent.Timeout(generation = 1L))

        assertFalse(machine.snapshot.isVisible)
        assertEquals(
            listOf(
                LoadingEffect.CancelTimeout(1L),
                LoadingEffect.Render(machine.snapshot),
                LoadingEffect.NotifyDismiss(1L, LoadingDismissReason.TIMEOUT),
            ),
            effects,
        )
    }

    @Test
    fun timeoutAfterManualHideHasNoEffect() {
        val machine = LoadingStateMachine()
        machine.dispatch(LoadingEvent.Show(LoadingRequest(timeoutMillis = 3_000L)))
        machine.dispatch(LoadingEvent.Hide)

        assertTrue(machine.dispatch(LoadingEvent.Timeout(1L)).isEmpty())
        assertFalse(machine.snapshot.isVisible)
    }

    @Test
    fun nonPositiveTimeoutDoesNotScheduleAutomaticDismissal() {
        listOf<Long?>(null, 0L, -1L).forEach { timeout ->
            val machine = LoadingStateMachine()

            val effects = machine.dispatch(
                LoadingEvent.Show(LoadingRequest(timeoutMillis = timeout))
            )

            assertTrue(machine.snapshot.isVisible)
            assertTrue(effects.none { it is LoadingEffect.ScheduleTimeout })
        }
    }

    @Test
    fun updateMessageOnlyMutatesVisibleRequest() {
        val machine = LoadingStateMachine()

        val hiddenResult = machine.dispatch(LoadingEvent.UpdateMessage("hidden"))
        machine.dispatch(LoadingEvent.Show(LoadingRequest(message = "before")))
        val visibleResult = machine.dispatch(LoadingEvent.UpdateMessage("after"))

        assertTrue(hiddenResult.isEmpty())
        assertEquals(
            listOf(LoadingEffect.Render(machine.snapshot)),
            visibleResult,
        )
        assertEquals("after", machine.snapshot.request?.message)
    }

    @Test
    fun destroyDismissesVisibleRequestOnceAndRejectsLaterEvents() {
        val machine = LoadingStateMachine()
        machine.dispatch(LoadingEvent.Show(LoadingRequest(message = "A", timeoutMillis = 1_000L)))

        val destroyEffects = machine.dispatch(LoadingEvent.Destroy)
        val secondDestroy = machine.dispatch(LoadingEvent.Destroy)
        val staleTimeout = machine.dispatch(LoadingEvent.Timeout(1L))

        assertTrue(machine.snapshot.isDestroyed)
        assertEquals(
            listOf(
                LoadingEffect.CancelTimeout(1L),
                LoadingEffect.Render(machine.snapshot),
                LoadingEffect.NotifyDismiss(1L, LoadingDismissReason.DESTROYED),
            ),
            destroyEffects,
        )
        assertTrue(secondDestroy.isEmpty())
        assertTrue(staleTimeout.isEmpty())
    }

    @Test
    fun eachGenerationProducesAtMostOneDismissNotice() {
        val machine = LoadingStateMachine()
        val effects = mutableListOf<LoadingEffect>()

        effects += machine.dispatch(LoadingEvent.Show(LoadingRequest(message = "A")))
        effects += machine.dispatch(LoadingEvent.Show(LoadingRequest(message = "B")))
        effects += machine.dispatch(LoadingEvent.Timeout(1L))
        effects += machine.dispatch(LoadingEvent.Hide)
        effects += machine.dispatch(LoadingEvent.Hide)

        val notices = effects.filterIsInstance<LoadingEffect.NotifyDismiss>()
        assertEquals(
            listOf(
                LoadingEffect.NotifyDismiss(1L, LoadingDismissReason.REPLACED),
                LoadingEffect.NotifyDismiss(2L, LoadingDismissReason.MANUAL),
            ),
            notices,
        )
    }

    @Test
    fun rapidShowHideSequenceEndsInLatestDeterministicState() {
        val machine = LoadingStateMachine()

        repeat(50) { index ->
            machine.dispatch(LoadingEvent.Show(LoadingRequest(message = "$index")))
            machine.dispatch(LoadingEvent.Hide)
        }

        assertFalse(machine.snapshot.isVisible)
        assertEquals(50L, machine.snapshot.generation)
        assertIs<LoadingMachineState.Hidden>(machine.state)
    }
}

