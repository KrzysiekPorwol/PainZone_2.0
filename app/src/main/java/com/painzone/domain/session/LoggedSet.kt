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
) {
    init {
        require(order >= 1) { "order must be >= 1" }
        require(reps >= 1) { "reps must be >= 1" }
        require(weight >= 0) { "weight must be >= 0" }
    }

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
        ): LoggedSet =
            LoggedSet(
                id = 0L,
                sessionExerciseSnapshotId = sessionExerciseSnapshotId,
                order = order,
                reps = reps,
                weight = weight,
                rpe = rpe,
                completedAt = now,
            )
    }
}