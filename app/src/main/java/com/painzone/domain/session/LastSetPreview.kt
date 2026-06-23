package com.painzone.domain.session

import java.time.Instant

// One logged set from an exercise's most recent *prior* session — backs the per-series
// inline hint (S9 LastSetPreview): series K shows what was logged for series K last time.
// Plain projection, no invariants: it only ever mirrors an already-validated LoggedSet.
data class LastSetPreview(
    val reps: Int,
    val weight: Double,
    val rpe: Rpe?,
    val completedAt: Instant,
)
