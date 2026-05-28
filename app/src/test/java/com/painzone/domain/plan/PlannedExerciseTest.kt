package com.painzone.domain.plan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PlannedExerciseTest {

    // --- create ---------------------------------------------------------------

    @Test
    fun `create returns planned exercise with id=0 and given fields`() {
        val pe = PlannedExercise.create(
            plannedDayId = 5L,
            exerciseId = 9L,
            targetReps = listOf(10, 9, 8),
            restSeconds = 120,
            order = 0,
        )

        assertEquals(0L, pe.id)
        assertEquals(5L, pe.plannedDayId)
        assertEquals(9L, pe.exerciseId)
        assertEquals(listOf(10, 9, 8), pe.targetReps)
        assertEquals(120, pe.restSeconds)
        assertEquals(0, pe.order)
    }

    @Test
    fun `sets is derived from targetReps size`() {
        val pe = PlannedExercise.create(1L, 1L, listOf(10, 10, 10, 10), null, 0)
        assertEquals(4, pe.sets)
    }

    @Test
    fun `null restSeconds is allowed`() {
        val pe = PlannedExercise.create(1L, 1L, listOf(8), null, 0)
        assertNull(pe.restSeconds)
    }

    @Test
    fun `zero restSeconds is allowed`() {
        val pe = PlannedExercise.create(1L, 1L, listOf(8), 0, 0)
        assertEquals(0, pe.restSeconds)
    }

    // --- init invariants ------------------------------------------------------

    @Test(expected = IllegalArgumentException::class)
    fun `create with empty targetReps throws`() {
        PlannedExercise.create(1L, 1L, emptyList(), 60, 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create with any targetReps element less than 1 throws`() {
        PlannedExercise.create(1L, 1L, listOf(10, 0, 8), 60, 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create with negative targetReps element throws`() {
        PlannedExercise.create(1L, 1L, listOf(-1), 60, 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create with negative restSeconds throws`() {
        PlannedExercise.create(1L, 1L, listOf(10), -1, 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create with negative order throws`() {
        PlannedExercise.create(1L, 1L, listOf(10), 60, -1)
    }

    // --- updateParams ---------------------------------------------------------

    @Test
    fun `updateParams replaces targetReps and restSeconds and keeps other fields`() {
        val pe = PlannedExercise.create(5L, 9L, listOf(10, 10), 90, 2).copy(id = 17L)
        val updated = pe.updateParams(targetReps = listOf(8, 7, 6), restSeconds = null)

        assertEquals(17L, updated.id)
        assertEquals(5L, updated.plannedDayId)
        assertEquals(9L, updated.exerciseId)
        assertEquals(2, updated.order)
        assertEquals(listOf(8, 7, 6), updated.targetReps)
        assertNull(updated.restSeconds)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `updateParams with empty targetReps throws`() {
        val pe = PlannedExercise.create(1L, 1L, listOf(10), 60, 0)
        pe.updateParams(targetReps = emptyList(), restSeconds = 60)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `updateParams with rep less than 1 throws`() {
        val pe = PlannedExercise.create(1L, 1L, listOf(10), 60, 0)
        pe.updateParams(targetReps = listOf(10, 0), restSeconds = 60)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `updateParams with negative restSeconds throws`() {
        val pe = PlannedExercise.create(1L, 1L, listOf(10), 60, 0)
        pe.updateParams(targetReps = listOf(10), restSeconds = -5)
    }

    // --- reorder --------------------------------------------------------------

    @Test
    fun `reorder updates order and keeps other fields`() {
        val pe = PlannedExercise.create(5L, 9L, listOf(10, 10), 90, 0).copy(id = 17L)
        val moved = pe.reorder(4)

        assertEquals(17L, moved.id)
        assertEquals(5L, moved.plannedDayId)
        assertEquals(9L, moved.exerciseId)
        assertEquals(listOf(10, 10), moved.targetReps)
        assertEquals(90, moved.restSeconds)
        assertEquals(4, moved.order)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `reorder to negative throws`() {
        PlannedExercise.create(1L, 1L, listOf(10), 60, 0).reorder(-1)
    }
}