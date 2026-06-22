package com.painzone.data.session

import com.painzone.domain.session.LoggedSet
import com.painzone.domain.session.SessionExerciseSnapshot
import com.painzone.domain.session.WorkoutSession

fun WorkoutSessionEntity.toDomain(): WorkoutSession = WorkoutSession(
    id = id,
    plannedDayId = plannedDayId,
    planNameSnapshot = planNameSnapshot,
    dayNameSnapshot = dayNameSnapshot,
    startedAt = startedAt,
    finishedAt = finishedAt,
)

fun WorkoutSession.toEntity(): WorkoutSessionEntity = WorkoutSessionEntity(
    id = id,
    plannedDayId = plannedDayId,
    planNameSnapshot = planNameSnapshot,
    dayNameSnapshot = dayNameSnapshot,
    startedAt = startedAt,
    finishedAt = finishedAt,
)

fun SessionExerciseSnapshotEntity.toDomain(): SessionExerciseSnapshot = SessionExerciseSnapshot(
    id = id,
    sessionId = sessionId,
    exerciseId = exerciseId,
    exerciseNameSnapshot = exerciseNameSnapshot,
    muscleGroupSnapshot = muscleGroupSnapshot,
    order = order,
    plannedTargetReps = plannedTargetReps,
    plannedRestSeconds = plannedRestSeconds,
)

fun SessionExerciseSnapshot.toEntity(): SessionExerciseSnapshotEntity = SessionExerciseSnapshotEntity(
    id = id,
    sessionId = sessionId,
    exerciseId = exerciseId,
    exerciseNameSnapshot = exerciseNameSnapshot,
    muscleGroupSnapshot = muscleGroupSnapshot,
    order = order,
    plannedTargetReps = plannedTargetReps,
    plannedRestSeconds = plannedRestSeconds,
)

fun LoggedSetEntity.toDomain(): LoggedSet = LoggedSet(
    id = id,
    sessionExerciseSnapshotId = sessionExerciseSnapshotId,
    order = order,
    reps = reps,
    weight = weight,
    rpe = rpe,
    completedAt = completedAt,
)

fun LoggedSet.toEntity(): LoggedSetEntity = LoggedSetEntity(
    id = id,
    sessionExerciseSnapshotId = sessionExerciseSnapshotId,
    order = order,
    reps = reps,
    weight = weight,
    rpe = rpe,
    completedAt = completedAt,
)
