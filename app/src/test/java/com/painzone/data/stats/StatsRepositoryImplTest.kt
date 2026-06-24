package com.painzone.data.stats

import com.painzone.domain.session.Rpe
import com.painzone.domain.stats.StatsPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class StatsRepositoryImplTest {

    private lateinit var dao: FakeStatsDao
    private lateinit var repo: StatsRepositoryImpl

    @Before
    fun setUp() {
        dao = FakeStatsDao()
        repo = StatsRepositoryImpl(dao)
    }

    private val now: Instant = Instant.now()

    // Seeds one logged set for an exercise within a session that started `daysAgo` days ago.
    private fun seed(
        setId: Long,
        exerciseId: Long,
        sessionId: Long,
        daysAgo: Long,
        order: Int = 1,
        reps: Int = 8,
        weight: Double = 60.0,
        rpe: Rpe? = null,
        rest: Int? = null,
        finished: Boolean = true,
    ) {
        val startedAt = now.minusSeconds(daysAgo * 86_400)
        dao.entries += FakeStatsDao.Entry(
            exerciseId = exerciseId,
            finished = finished,
            startedAt = startedAt,
            row = StatsSetRow(
                setId = setId,
                sessionId = sessionId,
                sessionStartedAt = startedAt,
                planNameSnapshot = "PPL",
                dayNameSnapshot = "Push",
                order = order,
                reps = reps,
                weight = weight,
                rpe = rpe,
                restBeforeSeconds = rest,
                completedAt = startedAt.plusSeconds(order * 60L),
            ),
        )
    }

    @Test
    fun `maps all row fields to domain`() = runTest {
        seed(setId = 5, exerciseId = 1, sessionId = 9, daysAgo = 1, order = 2, reps = 10, weight = 82.5, rpe = Rpe.Hard, rest = 120)

        val set = repo.observeSets(1, StatsPeriod.ALL).first().single()

        assertEquals(5L, set.setId)
        assertEquals(9L, set.sessionId)
        assertEquals("PPL", set.planNameSnapshot)
        assertEquals("Push", set.dayNameSnapshot)
        assertEquals(2, set.order)
        assertEquals(10, set.reps)
        assertEquals(82.5, set.weight, 0.0)
        assertEquals(Rpe.Hard, set.rpe)
        assertEquals(120, set.restBeforeSeconds)
    }

    @Test
    fun `period window excludes sets older than the cutoff`() = runTest {
        seed(setId = 1, exerciseId = 1, sessionId = 1, daysAgo = 10)
        seed(setId = 2, exerciseId = 1, sessionId = 2, daysAgo = 40)

        val ids = repo.observeSets(1, StatsPeriod.LAST_30_DAYS).first().map { it.setId }

        assertEquals(listOf(1L), ids)
    }

    @Test
    fun `ALL returns every finished set regardless of age`() = runTest {
        seed(setId = 1, exerciseId = 1, sessionId = 1, daysAgo = 10)
        seed(setId = 2, exerciseId = 1, sessionId = 2, daysAgo = 400)

        val ids = repo.observeSets(1, StatsPeriod.ALL).first().map { it.setId }.sorted()

        assertEquals(listOf(1L, 2L), ids)
    }

    @Test
    fun `in-progress session sets are excluded`() = runTest {
        seed(setId = 1, exerciseId = 1, sessionId = 1, daysAgo = 1, finished = true)
        seed(setId = 2, exerciseId = 1, sessionId = 2, daysAgo = 0, finished = false)

        val ids = repo.observeSets(1, StatsPeriod.ALL).first().map { it.setId }

        assertEquals(listOf(1L), ids)
    }

    @Test
    fun `only the requested exercise is returned`() = runTest {
        seed(setId = 1, exerciseId = 1, sessionId = 1, daysAgo = 1)
        seed(setId = 2, exerciseId = 2, sessionId = 2, daysAgo = 1)

        val ids = repo.observeSets(1, StatsPeriod.ALL).first().map { it.setId }

        assertEquals(listOf(1L), ids)
    }

    @Test
    fun `ordered newest session first then by series order`() = runTest {
        // Older session (2 days ago), two series.
        seed(setId = 10, exerciseId = 1, sessionId = 1, daysAgo = 2, order = 1)
        seed(setId = 11, exerciseId = 1, sessionId = 1, daysAgo = 2, order = 2)
        // Newer session (1 day ago), two series.
        seed(setId = 20, exerciseId = 1, sessionId = 2, daysAgo = 1, order = 1)
        seed(setId = 21, exerciseId = 1, sessionId = 2, daysAgo = 1, order = 2)

        val ids = repo.observeSets(1, StatsPeriod.ALL).first().map { it.setId }

        assertEquals(listOf(20L, 21L, 10L, 11L), ids)
    }

    @Test
    fun `empty when the exercise has no history`() = runTest {
        assertTrue(repo.observeSets(99, StatsPeriod.ALL).first().isEmpty())
    }
}

// In-memory stand-in for StatsDao replicating the SQL WHERE/ORDER of observeSetsForExercise:
// finished sessions only, exercise match, optional `since` lower bound, newest session first.
private class FakeStatsDao : StatsDao {
    data class Entry(
        val exerciseId: Long,
        val finished: Boolean,
        val startedAt: Instant,
        val row: StatsSetRow,
    )

    val entries = mutableListOf<Entry>()

    override fun observeSetsForExercise(exerciseId: Long, since: Instant?): Flow<List<StatsSetRow>> =
        flowOf(
            entries
                .filter { it.exerciseId == exerciseId && it.finished }
                .filter { since == null || !it.startedAt.isBefore(since) }
                .sortedWith(compareByDescending<Entry> { it.startedAt }.thenBy { it.row.order })
                .map { it.row },
        )
}
