package com.painzone.domain.exercise

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class ExerciseTest {

    private val t0: Instant = Instant.parse("2026-01-01T10:00:00Z")
    private val t1: Instant = Instant.parse("2026-01-02T10:00:00Z")

    // --- create ---------------------------------------------------------------

    @Test
    fun `create returns active exercise with trimmed name and id=0`() {
        val e = Exercise.create(name = "Bench press", muscleGroup = MuscleGroup.Chest, now = t0)

        assertEquals(0L, e.id)
        assertEquals("Bench press", e.name)
        assertEquals(MuscleGroup.Chest, e.muscleGroup)
        assertEquals(t0, e.createdAt)
        assertNull(e.deletedAt)
        assertTrue(e.isActive)
    }

    @Test
    fun `create trims surrounding whitespace`() {
        val e = Exercise.create(name = "  Squat  ", muscleGroup = MuscleGroup.Legs, now = t0)
        assertEquals("Squat", e.name)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create with blank name throws`() {
        Exercise.create(name = "", muscleGroup = MuscleGroup.Chest, now = t0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create with whitespace-only name throws`() {
        Exercise.create(name = "   ", muscleGroup = MuscleGroup.Chest, now = t0)
    }

    // --- init invariants (direct construction) --------------------------------

    @Test(expected = IllegalArgumentException::class)
    fun `direct construction with untrimmed name throws`() {
        Exercise(id = 1L, name = " Bench ", muscleGroup = MuscleGroup.Chest, createdAt = t0, deletedAt = null)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `direct construction with deletedAt before createdAt throws`() {
        Exercise(
            id = 1L,
            name = "Bench",
            muscleGroup = MuscleGroup.Chest,
            createdAt = t1,
            deletedAt = t0,
        )
    }

    @Test
    fun `direct construction with deletedAt equal to createdAt is allowed`() {
        val e = Exercise(
            id = 1L,
            name = "Bench",
            muscleGroup = MuscleGroup.Chest,
            createdAt = t0,
            deletedAt = t0,
        )
        assertFalse(e.isActive)
    }

    // --- rename ---------------------------------------------------------------

    @Test
    fun `rename updates name and keeps id and timestamps`() {
        val e = Exercise.create("Bench", MuscleGroup.Chest, t0).copy(id = 7L)
        val renamed = e.rename("Bench press")

        assertEquals(7L, renamed.id)
        assertEquals("Bench press", renamed.name)
        assertEquals(t0, renamed.createdAt)
        assertNull(renamed.deletedAt)
    }

    @Test
    fun `rename trims new name`() {
        val e = Exercise.create("Bench", MuscleGroup.Chest, t0)
        assertEquals("Squat", e.rename("  Squat  ").name)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rename to blank throws`() {
        Exercise.create("Bench", MuscleGroup.Chest, t0).rename("   ")
    }

    // --- softDelete -----------------------------------------------------------

    @Test
    fun `softDelete on active sets deletedAt and flips isActive`() {
        val e = Exercise.create("Bench", MuscleGroup.Chest, t0)
        val deleted = e.softDelete(t1)

        assertEquals(t1, deleted.deletedAt)
        assertFalse(deleted.isActive)
    }

    @Test(expected = IllegalStateException::class)
    fun `softDelete on already soft-deleted throws`() {
        val e = Exercise.create("Bench", MuscleGroup.Chest, t0).softDelete(t1)
        e.softDelete(t1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `softDelete with timestamp before createdAt throws`() {
        val e = Exercise.create("Bench", MuscleGroup.Chest, t1)
        e.softDelete(t0)
    }
}