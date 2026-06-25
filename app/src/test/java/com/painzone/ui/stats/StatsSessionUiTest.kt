package com.painzone.ui.stats

import com.painzone.domain.session.Rpe
import com.painzone.domain.stats.StatsSet
import org.junit.Assert.assertEquals
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Test

class StatsSessionUiTest {

    private val utc = ZoneId.of("UTC")

    private fun set(
        setId: Long,
        sessionId: Long,
        order: Int,
        reps: Int,
        weight: Double,
        rpe: Rpe? = null,
        restBeforeSeconds: Int? = null,
        startedAt: String = "2026-06-22T08:00:00Z",
    ) = StatsSet(
        setId = setId,
        sessionId = sessionId,
        sessionStartedAt = Instant.parse(startedAt),
        planNameSnapshot = "Push/Pull/Legs",
        dayNameSnapshot = "Push A",
        order = order,
        reps = reps,
        weight = weight,
        rpe = rpe,
        restBeforeSeconds = restBeforeSeconds,
        completedAt = Instant.parse(startedAt),
    )

    @Test
    fun `groups sets by session preserving order`() {
        val flat = listOf(
            set(20, sessionId = 2, order = 1, reps = 8, weight = 80.0, startedAt = "2026-06-22T08:00:00Z"),
            set(21, sessionId = 2, order = 2, reps = 8, weight = 80.0, restBeforeSeconds = 120, startedAt = "2026-06-22T08:00:00Z"),
            set(10, sessionId = 1, order = 1, reps = 8, weight = 77.5, startedAt = "2026-06-15T08:00:00Z"),
        )
        val sessions = flat.toSessionUi(utc)
        assertEquals(listOf(2L, 1L), sessions.map { it.sessionId })
        assertEquals(2, sessions[0].sets.size)
        assertEquals(1, sessions[1].sets.size)
    }

    @Test
    fun `header is date · plan · day`() {
        val sessions = listOf(set(1, sessionId = 1, order = 1, reps = 5, weight = 60.0)).toSessionUi(utc)
        assertEquals("22.06 · Push/Pull/Legs · Push A", sessions[0].header)
    }

    @Test
    fun `first series has no rest, later series show rest`() {
        val sessions = listOf(
            set(1, sessionId = 1, order = 1, reps = 8, weight = 80.0, restBeforeSeconds = 90),
            set(2, sessionId = 1, order = 2, reps = 8, weight = 80.0, restBeforeSeconds = 120),
        ).toSessionUi(utc)
        // Order 1 ignores any rest value — it opened the exercise.
        assertEquals("8 × 80 kg · —", sessions[0].sets[0].text)
        assertEquals("8 × 80 kg · po 120s odpocz.", sessions[0].sets[1].text)
    }

    @Test
    fun `missing rest on later series falls back to dash`() {
        val sessions = listOf(
            set(1, sessionId = 1, order = 1, reps = 8, weight = 80.0),
            set(2, sessionId = 1, order = 2, reps = 8, weight = 80.0, restBeforeSeconds = null),
        ).toSessionUi(utc)
        assertEquals("8 × 80 kg · —", sessions[0].sets[1].text)
    }

    @Test
    fun `rpe is included when present and weight half-steps preserved`() {
        val sessions = listOf(
            set(1, sessionId = 1, order = 2, reps = 6, weight = 82.5, rpe = Rpe.Hard, restBeforeSeconds = 150),
        ).toSessionUi(utc)
        assertEquals("6 × 82.5 kg · RPE Ciężka · po 150s odpocz.", sessions[0].sets[0].text)
    }

    // --- Best set (M4.4) ---

    private fun bestText(sets: List<StatsSet>, today: String) =
        sets.toBestSetUi(LocalDate.parse(today), utc).text

    @Test
    fun `best set is highest estimated 1RM, not highest weight`() {
        // 10×100 → 1RM est 133.3; 1×140 → 1RM est 144.7. Fewer reps heavier wins on 1RM.
        val sets = listOf(
            set(1, sessionId = 1, order = 1, reps = 10, weight = 100.0, startedAt = "2026-06-22T08:00:00Z"),
            set(2, sessionId = 1, order = 2, reps = 1, weight = 140.0, startedAt = "2026-06-22T08:00:00Z"),
        )
        assertEquals("Best: 1 × 140 kg · 1RM≈145 kg · dziś", bestText(sets, "2026-06-22"))
    }

    @Test
    fun `best set ties broken by newest session`() {
        // Same 1RM est (5×100) in two sessions; the newer one (sessionId 2) wins and sets the date.
        val sets = listOf(
            set(20, sessionId = 2, order = 1, reps = 5, weight = 100.0, startedAt = "2026-06-22T08:00:00Z"),
            set(10, sessionId = 1, order = 1, reps = 5, weight = 100.0, startedAt = "2026-06-15T08:00:00Z"),
        )
        assertEquals("Best: 5 × 100 kg · 1RM≈117 kg · dziś", bestText(sets, "2026-06-22"))
    }

    @Test
    fun `days-ago label reflects the best set session date`() {
        val sets = listOf(
            set(1, sessionId = 1, order = 1, reps = 8, weight = 80.0, startedAt = "2026-06-20T08:00:00Z"),
        )
        assertEquals("Best: 8 × 80 kg · 1RM≈101 kg · 2 dni temu", bestText(sets, "2026-06-22"))
    }
}
