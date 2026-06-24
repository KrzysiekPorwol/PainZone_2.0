package com.painzone.domain.stats

import com.painzone.domain.session.Rpe
import java.time.Instant

// One logged set as seen by Stats Lite: the set values plus the snapshot session context
// (date · plan · day) the S10 list groups by. A flat read projection — no invariants of its
// own; the source rows were validated when logged. 1RM est. (M4.2) and best-set highlight
// (M4.4) are computed on top of a list of these.
data class StatsSet(
    val setId: Long,
    val sessionId: Long,
    val sessionStartedAt: Instant,
    val planNameSnapshot: String,
    val dayNameSnapshot: String,
    val order: Int,
    val reps: Int,
    val weight: Double,
    val rpe: Rpe?,
    val restBeforeSeconds: Int?,
    val completedAt: Instant,
)
