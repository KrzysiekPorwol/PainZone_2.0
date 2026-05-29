package com.painzone.domain.session

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class WorkoutSessionTest {

    private val start = Instant.parse("2026-05-29T10:00:00Z")

    // --- create ---------------------------------------------------------------

    @Test
    fun `create returns in-progress session with id=0 and given fields`() {
        val s = WorkoutSession.create(
            plannedDayId = 7L,
            planNameSnapshot = "Push Pull Legs",
            dayNameSnapshot = "Push A",
            startedAt = start,
        )

        assertEquals(0L, s.id)
        assertEquals(7L, s.plannedDayId)
        assertEquals("Push Pull Legs", s.planNameSnapshot)
        assertEquals("Push A", s.dayNameSnapshot)
        assertEquals(start, s.startedAt)
        assertNull(s.finishedAt)
        assertTrue(s.isInProgress)
    }

    @Test
    fun `create trims snapshot names`() {
        val s = WorkoutSession.create(1L, "  PPL  ", "  Push  ", start)
        assertEquals("PPL", s.planNameSnapshot)
        assertEquals("Push", s.dayNameSnapshot)
    }

    // --- init invariants ------------------------------------------------------

    @Test(expected = IllegalArgumentException::class)
    fun `blank planNameSnapshot throws`() {
        WorkoutSession.create(1L, "   ", "Push", start)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank dayNameSnapshot throws`() {
        WorkoutSession.create(1L, "PPL", "   ", start)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `finishedAt before startedAt throws`() {
        WorkoutSession(
            id = 1L,
            plannedDayId = 1L,
            planNameSnapshot = "PPL",
            dayNameSnapshot = "Push",
            startedAt = start,
            finishedAt = start.minusSeconds(1),
        )
    }

    @Test
    fun `finishedAt equal to startedAt is allowed`() {
        val s = WorkoutSession(
            id = 1L,
            plannedDayId = 1L,
            planNameSnapshot = "PPL",
            dayNameSnapshot = "Push",
            startedAt = start,
            finishedAt = start,
        )
        assertEquals(start, s.finishedAt)
    }

    // --- isInProgress / finish ------------------------------------------------

    @Test
    fun `finish sets finishedAt and flips isInProgress`() {
        val s = WorkoutSession.create(1L, "PPL", "Push", start)
        val end = start.plusSeconds(3600)
        val finished = s.finish(end)

        assertEquals(end, finished.finishedAt)
        assertFalse(finished.isInProgress)
        assertTrue(s.isInProgress)
    }

    @Test
    fun `finish keeps other fields`() {
        val s = WorkoutSession.create(7L, "PPL", "Push", start).copy(id = 42L)
        val finished = s.finish(start.plusSeconds(60))

        assertEquals(42L, finished.id)
        assertEquals(7L, finished.plannedDayId)
        assertEquals("PPL", finished.planNameSnapshot)
        assertEquals("Push", finished.dayNameSnapshot)
        assertEquals(start, finished.startedAt)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `finish before startedAt throws`() {
        WorkoutSession.create(1L, "PPL", "Push", start).finish(start.minusSeconds(1))
    }
}