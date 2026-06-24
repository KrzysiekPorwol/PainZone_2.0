package com.painzone.data.stats

import com.painzone.domain.stats.StatsSet

fun StatsSetRow.toDomain(): StatsSet = StatsSet(
    setId = setId,
    sessionId = sessionId,
    sessionStartedAt = sessionStartedAt,
    planNameSnapshot = planNameSnapshot,
    dayNameSnapshot = dayNameSnapshot,
    order = order,
    reps = reps,
    weight = weight,
    rpe = rpe,
    restBeforeSeconds = restBeforeSeconds,
    completedAt = completedAt,
)
