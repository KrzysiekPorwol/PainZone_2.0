package com.painzone.data.exercise

import com.painzone.domain.exercise.Exercise
import com.painzone.domain.exercise.MuscleGroup
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class ExerciseMapperTest {

    private val t0: Instant = Instant.parse("2026-01-01T10:00:00Z")
    private val t1: Instant = Instant.parse("2026-01-02T10:00:00Z")

    @Test
    fun `entity to domain preserves all fields for active record`() {
        val entity = ExerciseEntity(
            id = 7L,
            name = "Bench press",
            muscleGroup = MuscleGroup.Chest,
            createdAt = t0,
            deletedAt = null,
        )

        val domain = entity.toDomain()

        assertEquals(7L, domain.id)
        assertEquals("Bench press", domain.name)
        assertEquals(MuscleGroup.Chest, domain.muscleGroup)
        assertEquals(t0, domain.createdAt)
        assertEquals(null, domain.deletedAt)
    }

    @Test
    fun `entity to domain preserves deletedAt for soft-deleted record`() {
        val entity = ExerciseEntity(
            id = 3L,
            name = "Squat",
            muscleGroup = MuscleGroup.Legs,
            createdAt = t0,
            deletedAt = t1,
        )

        val domain = entity.toDomain()

        assertEquals(t1, domain.deletedAt)
    }

    @Test
    fun `domain to entity preserves all fields and id=0 for new record`() {
        val domain = Exercise.create(name = "Deadlift", muscleGroup = MuscleGroup.Back, now = t0)

        val entity = domain.toEntity()

        assertEquals(0L, entity.id)
        assertEquals("Deadlift", entity.name)
        assertEquals(MuscleGroup.Back, entity.muscleGroup)
        assertEquals(t0, entity.createdAt)
        assertEquals(null, entity.deletedAt)
    }

    @Test
    fun `roundtrip entity to domain to entity is identity`() {
        val original = ExerciseEntity(
            id = 42L,
            name = "Overhead press",
            muscleGroup = MuscleGroup.Shoulders,
            createdAt = t0,
            deletedAt = t1,
        )

        val roundtripped = original.toDomain().toEntity()

        assertEquals(original, roundtripped)
    }
}
