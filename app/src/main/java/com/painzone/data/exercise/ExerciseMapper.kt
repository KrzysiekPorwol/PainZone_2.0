package com.painzone.data.exercise

import com.painzone.domain.exercise.Exercise

fun ExerciseEntity.toDomain(): Exercise = Exercise(
    id = id,
    name = name,
    muscleGroup = muscleGroup,
    createdAt = createdAt,
    deletedAt = deletedAt,
)

fun Exercise.toEntity(): ExerciseEntity = ExerciseEntity(
    id = id,
    name = name,
    muscleGroup = muscleGroup,
    createdAt = createdAt,
    deletedAt = deletedAt,
)
