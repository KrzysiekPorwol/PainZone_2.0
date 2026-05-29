package com.painzone.domain.session

import com.painzone.domain.exercise.MuscleGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SessionExerciseSnapshotTest {

    private fun snapshot(
        exerciseNameSnapshot: String = "Bench Press",
        order: Int = 0,
        plannedTargetReps: List<Int> = listOf(10, 9, 8),
        plannedRestSeconds: Int? = 120,
    ) = SessionExerciseSnapshot.create(
        sessionId = 1L,
        exerciseId = 5L,
        exerciseNameSnapshot = exerciseNameSnapshot,
        muscleGroupSnapshot = MuscleGroup.Chest,
        order = order,
        plannedTargetReps = plannedTargetReps,
        plannedRestSeconds = plannedRestSeconds,
    )

    // --- create ---------------------------------------------------------------

    @Test
    fun `create returns snapshot with id=0 and given fields`() {
        val s = snapshot()

        assertEquals(0L, s.id)
        assertEquals(1L, s.sessionId)
        assertEquals(5L, s.exerciseId)
        assertEquals("Bench Press", s.exerciseNameSnapshot)
        assertEquals(MuscleGroup.Chest, s.muscleGroupSnapshot)
        assertEquals(0, s.order)
        assertEquals(listOf(10, 9, 8), s.plannedTargetReps)
        assertEquals(120, s.plannedRestSeconds)
    }

    @Test
    fun `create trims exerciseNameSnapshot`() {
        assertEquals("Bench Press", snapshot(exerciseNameSnapshot = "  Bench Press  ").exerciseNameSnapshot)
    }

    @Test
    fun `plannedSets is derived from plannedTargetReps size`() {
        assertEquals(3, snapshot(plannedTargetReps = listOf(10, 10, 10)).plannedSets)
    }

    @Test
    fun `null plannedRestSeconds is allowed`() {
        assertNull(snapshot(plannedRestSeconds = null).plannedRestSeconds)
    }

    @Test
    fun `zero plannedRestSeconds is allowed`() {
        assertEquals(0, snapshot(plannedRestSeconds = 0).plannedRestSeconds)
    }

    // --- init invariants ------------------------------------------------------

    @Test(expected = IllegalArgumentException::class)
    fun `blank exerciseNameSnapshot throws`() {
        snapshot(exerciseNameSnapshot = "   ")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative order throws`() {
        snapshot(order = -1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `empty plannedTargetReps throws`() {
        snapshot(plannedTargetReps = emptyList())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `plannedTargetReps element less than 1 throws`() {
        snapshot(plannedTargetReps = listOf(10, 0, 8))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative plannedRestSeconds throws`() {
        snapshot(plannedRestSeconds = -1)
    }
}