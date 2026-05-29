package com.painzone.domain.session

import java.time.Instant

data class WorkoutSession(
    val id: Long,
    // Nullable for ON DELETE SET NULL after plan hard-delete; create() requires non-null.
    val plannedDayId: Long?,
    val planNameSnapshot: String,
    val dayNameSnapshot: String,
    val startedAt: Instant,
    val finishedAt: Instant?,
) {
    init {
        require(planNameSnapshot == planNameSnapshot.trim()) { "planNameSnapshot must be trimmed" }
        require(planNameSnapshot.isNotEmpty()) { "planNameSnapshot must be non-blank" }
        require(dayNameSnapshot == dayNameSnapshot.trim()) { "dayNameSnapshot must be trimmed" }
        require(dayNameSnapshot.isNotEmpty()) { "dayNameSnapshot must be non-blank" }
        require(finishedAt == null || !finishedAt.isBefore(startedAt)) {
            "finishedAt must be >= startedAt"
        }
    }

    val isInProgress: Boolean get() = finishedAt == null

    fun finish(now: Instant): WorkoutSession = copy(finishedAt = now)

    companion object {
        fun create(
            plannedDayId: Long,
            planNameSnapshot: String,
            dayNameSnapshot: String,
            startedAt: Instant,
        ): WorkoutSession =
            WorkoutSession(
                id = 0L,
                plannedDayId = plannedDayId,
                planNameSnapshot = planNameSnapshot.trim(),
                dayNameSnapshot = dayNameSnapshot.trim(),
                startedAt = startedAt,
                finishedAt = null,
            )
    }
}