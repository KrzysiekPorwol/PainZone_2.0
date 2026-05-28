package com.painzone.domain.plan

data class PlannedExercise(
    val id: Long,
    val plannedDayId: Long,
    val exerciseId: Long,
    val order: Int,
    val targetReps: List<Int>,
    val restSeconds: Int?,
) {
    init {
        require(order >= 0) { "order must be >= 0" }
        require(targetReps.isNotEmpty()) { "targetReps must have at least 1 element" }
        require(targetReps.all { it >= 1 }) { "every targetReps element must be >= 1" }
        require(restSeconds == null || restSeconds >= 0) { "restSeconds must be null or >= 0" }
    }

    val sets: Int get() = targetReps.size

    fun updateParams(targetReps: List<Int>, restSeconds: Int?): PlannedExercise =
        copy(targetReps = targetReps, restSeconds = restSeconds)

    fun reorder(newOrder: Int): PlannedExercise = copy(order = newOrder)

    companion object {
        fun create(
            plannedDayId: Long,
            exerciseId: Long,
            targetReps: List<Int>,
            restSeconds: Int?,
            order: Int,
        ): PlannedExercise =
            PlannedExercise(
                id = 0L,
                plannedDayId = plannedDayId,
                exerciseId = exerciseId,
                order = order,
                targetReps = targetReps,
                restSeconds = restSeconds,
            )
    }
}