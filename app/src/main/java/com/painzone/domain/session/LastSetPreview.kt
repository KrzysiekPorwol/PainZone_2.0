package com.painzone.domain.session

import java.time.Instant

// Chronologically last logged set of an exercise from a *prior* session — backs the
// inline "reps × ciężar / RPE — N dni temu" hint (S9 LastSetPreview). Plain projection,
// no invariants: it only ever mirrors an already-validated LoggedSet.
data class LastSetPreview(
    val reps: Int,
    val weight: Double,
    val rpe: Rpe?,
    val completedAt: Instant,
)
