package io.github.yang1107.kuikly.loading.demo

import kotlin.test.Test
import kotlin.test.assertEquals

class AcceptanceScenarioTest {
    @Test
    fun parsesSupportedScenarioNames() {
        assertEquals(AcceptanceScenario.FULL_SCREEN, AcceptanceScenario.parse("full-screen"))
        assertEquals(AcceptanceScenario.TIMEOUT, AcceptanceScenario.parse("timeout"))
        assertEquals(AcceptanceScenario.CUSTOM_THEME, AcceptanceScenario.parse("custom-theme"))
        assertEquals(AcceptanceScenario.LOCAL, AcceptanceScenario.parse("local"))
    }

    @Test
    fun unknownOrBlankScenarioFallsBackToGallery() {
        assertEquals(AcceptanceScenario.GALLERY, AcceptanceScenario.parse(""))
        assertEquals(AcceptanceScenario.GALLERY, AcceptanceScenario.parse("unknown"))
        assertEquals(AcceptanceScenario.GALLERY, AcceptanceScenario.parse(null))
    }
}
