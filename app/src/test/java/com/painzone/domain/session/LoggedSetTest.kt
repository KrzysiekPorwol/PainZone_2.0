package com.painzone.domain.session

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class LoggedSetTest {

    private val now = Instant.parse("2026-05-29T10:15:00Z")

    // --- log ------------------------------------------------------------------

    @Test
    fun `log returns set with id=0 completedAt=now and given fields`() {
        val set = LoggedSet.log(
            sessionExerciseSnapshotId = 3L,
            order = 1,
            reps = 10,
            weight = 80.0,
            rpe = Rpe.Hard,
            now = now,
        )

        assertEquals(0L, set.id)
        assertEquals(3L, set.sessionExerciseSnapshotId)
        assertEquals(1, set.order)
        assertEquals(10, set.reps)
        assertEquals(80.0, set.weight, 0.0)
        assertEquals(Rpe.Hard, set.rpe)
        assertEquals(now, set.completedAt)
    }

    @Test
    fun `null rpe is allowed`() {
        assertNull(LoggedSet.log(1L, 1, 8, 60.0, null, now).rpe)
    }

    @Test
    fun `zero weight is allowed for bodyweight`() {
        assertEquals(0.0, LoggedSet.log(1L, 1, 12, 0.0, null, now).weight, 0.0)
    }

    @Test
    fun `restBeforeSeconds defaults to null for the first set`() {
        assertNull(LoggedSet.log(1L, 1, 10, 80.0, null, now).restBeforeSeconds)
    }

    @Test
    fun `restBeforeSeconds is carried through log`() {
        assertEquals(90, LoggedSet.log(1L, 2, 10, 80.0, null, now, restBeforeSeconds = 90).restBeforeSeconds)
    }

    @Test
    fun `zero restBeforeSeconds is allowed`() {
        assertEquals(0, LoggedSet.log(1L, 2, 10, 80.0, null, now, restBeforeSeconds = 0).restBeforeSeconds)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative restBeforeSeconds throws`() {
        LoggedSet.log(1L, 2, 10, 80.0, null, now, restBeforeSeconds = -1)
    }

    @Test
    fun `edit preserves restBeforeSeconds`() {
        val set = LoggedSet.log(1L, 2, 10, 80.0, null, now, restBeforeSeconds = 75)
        assertEquals(75, set.edit(reps = 8, weight = 85.0, rpe = Rpe.Hard).restBeforeSeconds)
    }

    // --- init invariants ------------------------------------------------------

    @Test(expected = IllegalArgumentException::class)
    fun `order less than 1 throws`() {
        LoggedSet.log(1L, 0, 10, 80.0, null, now)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `reps less than 1 throws`() {
        LoggedSet.log(1L, 1, 0, 80.0, null, now)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative weight throws`() {
        LoggedSet.log(1L, 1, 10, -0.5, null, now)
    }

    // --- edit -----------------------------------------------------------------

    @Test
    fun `edit replaces reps weight rpe and keeps other fields`() {
        val set = LoggedSet.log(3L, 2, 10, 80.0, Rpe.Normal, now).copy(id = 11L)
        val edited = set.edit(reps = 8, weight = 85.0, rpe = null)

        assertEquals(11L, edited.id)
        assertEquals(3L, edited.sessionExerciseSnapshotId)
        assertEquals(2, edited.order)
        assertEquals(now, edited.completedAt)
        assertEquals(8, edited.reps)
        assertEquals(85.0, edited.weight, 0.0)
        assertNull(edited.rpe)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `edit with reps less than 1 throws`() {
        LoggedSet.log(1L, 1, 10, 80.0, null, now).edit(reps = 0, weight = 80.0, rpe = null)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `edit with negative weight throws`() {
        LoggedSet.log(1L, 1, 10, 80.0, null, now).edit(reps = 10, weight = -1.0, rpe = null)
    }
}