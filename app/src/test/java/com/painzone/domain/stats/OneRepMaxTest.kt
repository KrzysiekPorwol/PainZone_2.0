package com.painzone.domain.stats

import com.painzone.domain.session.Rpe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class OneRepMaxTest {

    @Test
    fun `applies Epley formula`() {
        // 100 × (1 + 5/30) = 116.666...
        assertEquals(116.6667, estimateOneRepMax(100.0, 5), 0.0001)
    }

    @Test
    fun `single rep follows the formula verbatim, not raw weight`() {
        // Documented Epley has no special case: 100 × (1 + 1/30) = 103.333...
        assertEquals(103.3333, estimateOneRepMax(100.0, 1), 0.0001)
    }

    @Test
    fun `reps divide as Double, not integer`() {
        // Guards against reps / 30 truncating to 0 for reps < 30.
        assertTrue(estimateOneRepMax(100.0, 10) > 100.0)
        assertEquals(133.3333, estimateOneRepMax(100.0, 10), 0.0001)
    }

    @Test
    fun `bodyweight zero is allowed and stays zero`() {
        assertEquals(0.0, estimateOneRepMax(0.0, 8), 0.0)
    }

    @Test
    fun `more reps at equal weight yields higher estimate`() {
        assertTrue(estimateOneRepMax(80.0, 8) > estimateOneRepMax(80.0, 5))
    }

    @Test
    fun `rejects reps below 1`() {
        assertThrows(IllegalArgumentException::class.java) { estimateOneRepMax(100.0, 0) }
    }

    @Test
    fun `rejects negative weight`() {
        assertThrows(IllegalArgumentException::class.java) { estimateOneRepMax(-1.0, 5) }
    }

    @Test
    fun `StatsSet extension delegates to the formula`() {
        val set = statsSet(weight = 120.0, reps = 3)
        assertEquals(estimateOneRepMax(120.0, 3), set.estimatedOneRepMax(), 0.0)
    }

    private fun statsSet(weight: Double, reps: Int) = StatsSet(
        setId = 1L,
        sessionId = 1L,
        sessionStartedAt = Instant.parse("2026-06-24T10:00:00Z"),
        planNameSnapshot = "Plan",
        dayNameSnapshot = "Day",
        order = 1,
        reps = reps,
        weight = weight,
        rpe = Rpe.Normal,
        restBeforeSeconds = null,
        completedAt = Instant.parse("2026-06-24T10:05:00Z"),
    )
}
