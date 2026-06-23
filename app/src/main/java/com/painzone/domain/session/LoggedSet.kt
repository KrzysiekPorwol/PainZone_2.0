package com.painzone.domain.session

import java.time.Instant

data class LoggedSet(
    val id: Long,
    val sessionExerciseSnapshotId: Long,
    val order: Int,
    val reps: Int,
    val weight: Double,
    val rpe: Rpe?,
    val completedAt: Instant,
    // Actual rest (seconds) measured before this set: now − previous set's completedAt.
    // null for the first set of an exercise (no rest precedes it). See ADR-0008.
    val restBeforeSeconds: Int? = null,
) {
    init {
        require(order >= 1) { "order must be >= 1" }
        require(reps >= 1) { "reps must be >= 1" }
        require(weight >= 0) { "weight must be >= 0" }
        require(restBeforeSeconds == null || restBeforeSeconds >= 0) {
            "restBeforeSeconds must be null or >= 0"
        }
    }

    // Editing reps/weight/rpe never touches completedAt or the recorded rest before the set.
    fun edit(reps: Int, weight: Double, rpe: Rpe?): LoggedSet =
        copy(reps = reps, weight = weight, rpe = rpe)

    companion object {
        fun log(
            sessionExerciseSnapshotId: Long,
            order: Int,
            reps: Int,
            weight: Double,
            rpe: Rpe?,
            now: Instant,
            restBeforeSeconds: Int? = null,
        ): LoggedSet =
            LoggedSet(
                id = 0L,
                sessionExerciseSnapshotId = sessionExerciseSnapshotId,
                order = order,
                reps = reps,
                weight = weight,
                rpe = rpe,
                completedAt = now,
                restBeforeSeconds = restBeforeSeconds,
            )
    }
}