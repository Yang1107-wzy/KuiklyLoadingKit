package io.github.yang1107.kuikly.loading

import com.tencent.kuikly.core.base.PagerScope
import com.tencent.kuikly.core.timer.clearTimeout
import com.tencent.kuikly.core.timer.setTimeout

internal class KuiklyLoadingScheduler(
    private val pagerScope: PagerScope,
) : LoadingScheduler {
    override fun schedule(
        delayMillis: Long,
        action: () -> Unit,
    ): LoadingScheduledTask {
        val safeDelay = delayMillis.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        val timeoutRef = pagerScope.setTimeout(safeDelay, action)
        return LoadingScheduledTask {
            pagerScope.clearTimeout(timeoutRef)
        }
    }
}
