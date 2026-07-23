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
        var cancelled = false
        var timeoutRef: String? = null

        fun scheduleNext(remainingMillis: Long) {
            val chunkMillis = remainingMillis.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
            timeoutRef = pagerScope.setTimeout(chunkMillis) {
                timeoutRef = null
                if (cancelled) {
                    return@setTimeout
                }
                val afterChunk = remainingMillis - chunkMillis
                if (afterChunk > 0L) {
                    scheduleNext(afterChunk)
                } else {
                    action()
                }
            }
        }

        scheduleNext(delayMillis)
        return LoadingScheduledTask {
            cancelled = true
            timeoutRef?.let(pagerScope::clearTimeout)
            timeoutRef = null
        }
    }
}
