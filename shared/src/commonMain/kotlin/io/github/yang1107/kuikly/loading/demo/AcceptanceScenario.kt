package io.github.yang1107.kuikly.loading.demo

/**
 * Reproducible, host-selectable states used only to capture acceptance
 * evidence from a real simulator. The default remains the interactive gallery.
 */
internal enum class AcceptanceScenario(
    val wireName: String,
) {
    GALLERY("gallery"),
    FULL_SCREEN("full-screen"),
    TIMEOUT("timeout"),
    CUSTOM_THEME("custom-theme"),
    LOCAL("local");

    companion object {
        fun parse(value: String?): AcceptanceScenario =
            entries.firstOrNull { it.wireName == value } ?: GALLERY
    }
}
