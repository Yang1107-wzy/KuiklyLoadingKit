package io.github.yang1107.kuikly.loading.demo

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import com.tencent.kuikly.core.render.android.expand.KuiklyRenderViewBaseDelegator
import com.tencent.kuikly.core.render.android.expand.KuiklyRenderViewBaseDelegatorDelegate

/**
 * Minimal Android host for the interactive Kuikly acceptance gallery.
 */
class MainActivity : Activity(), KuiklyRenderViewBaseDelegatorDelegate {
    private val renderDelegator = KuiklyRenderViewBaseDelegator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container: ViewGroup = FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
        setContentView(container)
        val pageData = intent
            .getStringExtra(ACCEPTANCE_SCENARIO_PARAM)
            ?.let { mapOf(ACCEPTANCE_SCENARIO_PARAM to it) }
            ?: emptyMap()
        renderDelegator.onAttach(
            container,
            "",
            PAGE_NAME,
            pageData,
        )
    }

    override fun onResume() {
        super.onResume()
        renderDelegator.onResume()
    }

    override fun onPause() {
        renderDelegator.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        renderDelegator.onDetach()
        super.onDestroy()
    }

    @Deprecated("Deprecated in Android SDK; delegated for Kuikly compatibility")
    override fun onBackPressed() {
        if (!renderDelegator.onBackPressed()) {
            super.onBackPressed()
        }
    }

    private companion object {
        const val PAGE_NAME: String = "LoadingGalleryPage"
        const val ACCEPTANCE_SCENARIO_PARAM: String = "acceptanceScenario"
    }
}
