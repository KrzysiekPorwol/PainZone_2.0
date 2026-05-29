package com.painzone.domain.session

import com.painzone.domain.exercise.MuscleGroup

data class SessionExerciseSnapshot(
    val id: Long,
    val sessionId: Long,
    // May reference a soft-deleted Exercise; history renders from the snapshot fields.
    val exerciseId: Long,
    val exerciseNameSnapshot: String,
    val muscleGroupSnapshot: MuscleGroup,
    val order: Int,
    val plannedTargetReps: List<Int>,
    val plannedRestSeconds: Int?,
) {
    init {
        require(exerciseNameSnapshot == exerciseNameSnapshot.trim()) {
            "exerciseNameSnapshot must be trimmed"
        }
        require(exerciseNameSnapshot.isNotEmpty()) { "exerciseNameSnapshot must be non-blank" }
        require(order >= 0) { "order must be >= 0" }
        require(plannedTargetReps.isNotEmpty()) { "plannedTargetReps must have at least 1 element" }
        require(plannedTargetReps.all { it >= 1 }) { "every plannedTargetReps element must be >= 1" }
        require(plannedRestSeconds == null || plannedRestSeconds >= 0) {
            "plannedRestSeconds must be null or >= 0"
        }
    }

    val plannedSets: Int get() = plannedTargetReps.size

    companion object {
        fun create(
            sessionId: Long,
            exerciseId: Long,
            exerciseNameSnapshot: String,
            muscleGroupSnapshot: MuscleGroup,
            order: Int,
            plannedTargetReps: List<Int>,
            plannedRestSeconds: Int?,
        ): SessionExerciseSnapshot =
            SessionExerciseSnapshot(
                id = 0L,
                sessionId = sessionId,
                exerciseId = exerciseId,
                exerciseNameSnapshot = exerciseNameSnapshot.trim(),
                muscleGroupSnapshot = muscleGroupSnapshot,
                order = order,
                plannedTargetReps = plannedTargetReps,
                plannedRestSeconds = plannedRestSeconds,
            )
    }
}