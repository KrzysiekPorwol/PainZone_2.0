package com.painzone.data.plan

import com.painzone.domain.plan.PlannedDay
import com.painzone.domain.plan.PlannedExercise
import com.painzone.domain.plan.TrainingPlan

fun TrainingPlanEntity.toDomain(): TrainingPlan = TrainingPlan(
    id = id,
    name = name,
    isActive = isActive,
    createdAt = createdAt,
)

fun TrainingPlan.toEntity(): TrainingPlanEntity = TrainingPlanEntity(
    id = id,
    name = name,
    isActive = isActive,
    createdAt = createdAt,
)

fun PlannedDayEntity.toDomain(): PlannedDay = PlannedDay(
    id = id,
    trainingPlanId = trainingPlanId,
    name = name,
    order = order,
)

fun PlannedDay.toEntity(): PlannedDayEntity = PlannedDayEntity(
    id = id,
    trainingPlanId = trainingPlanId,
    name = name,
    order = order,
)

fun PlannedExerciseEntity.toDomain(): PlannedExercise = PlannedExercise(
    id = id,
    plannedDayId = plannedDayId,
    exerciseId = exerciseId,
    order = order,
    targetReps = targetReps,
    restSeconds = restSeconds,
)

fun PlannedExercise.toEntity(): PlannedExerciseEntity = PlannedExerciseEntity(
    id = id,
    plannedDayId = plannedDayId,
    exerciseId = exerciseId,
    order = order,
    targetReps = targetReps,
    restSeconds = restSeconds,
)