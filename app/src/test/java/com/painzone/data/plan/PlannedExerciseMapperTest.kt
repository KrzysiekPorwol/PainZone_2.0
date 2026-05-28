package com.painzone.data.plan

import com.painzone.domain.plan.PlannedExercise
import org.junit.Assert.assertEquals
import org.junit.Test

class PlannedExerciseMapperTest {

    @Test
    fun `entity to domain preserves targetReps list and restSeconds`() {
        val entity = PlannedExerciseEntity(
            id = 11L,
            plannedDayId = 4L,
            exerciseId = 2L,
            order = 0,
            targetReps = listOf(10, 9, 8),
            restSeconds = 90,
        )

        val domain = entity.toDomain()

        assertEquals(listOf(10, 9, 8), domain.targetReps)
        assertEquals(90, domain.restSeconds)
        assertEquals(3, domain.sets)
    }

    @Test
    fun `entity to domain preserves null restSeconds`() {
        val entity = PlannedExerciseEntity(
            id = 1L,
            plannedDayId = 1L,
            exerciseId = 1L,
            order = 0,
            targetReps = listOf(5),
            restSeconds = null,
        )

        assertEquals(null, entity.toDomain().restSeconds)
    }

    @Test
    fun `domain to entity preserves id=0 for new item`() {
        val domain = PlannedExercise.create(
            plannedDayId = 8L,
            exerciseId = 3L,
            targetReps = listOf(12, 10, 8, 6),
            restSeconds = 120,
            order = 2,
        )

        val entity = domain.toEntity()

        assertEquals(0L, entity.id)
        assertEquals(8L, entity.plannedDayId)
        assertEquals(3L, entity.exerciseId)
        assertEquals(listOf(12, 10, 8, 6), entity.targetReps)
        assertEquals(120, entity.restSeconds)
        assertEquals(2, entity.order)
    }

    @Test
    fun `roundtrip is identity`() {
        val original = PlannedExerciseEntity(
            id = 42L,
            plannedDayId = 7L,
            exerciseId = 5L,
            order = 3,
            targetReps = listOf(8, 8, 8),
            restSeconds = 60,
        )

        assertEquals(original, original.toDomain().toEntity())
    }
}