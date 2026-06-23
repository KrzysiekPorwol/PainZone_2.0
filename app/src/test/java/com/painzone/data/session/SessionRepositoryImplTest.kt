package com.painzone.data.session

import com.painzone.data.exercise.ExerciseDao
import com.painzone.data.exercise.ExerciseEntity
import com.painzone.data.plan.PlannedDayDao
import com.painzone.data.plan.PlannedDayEntity
import com.painzone.data.plan.PlannedExerciseDao
import com.painzone.data.plan.PlannedExerciseEntity
import com.painzone.data.plan.TrainingPlanDao
import com.painzone.data.plan.TrainingPlanEntity
import com.painzone.data.plan.relation.PlanSummaryRow
import com.painzone.data.plan.relation.PlanWithDays
import com.painzone.data.session.relation.SessionWithDetail
import com.painzone.data.session.relation.SessionWithSnapshots
import com.painzone.data.session.relation.SnapshotWithLoggedSets
import com.painzone.domain.exercise.MuscleGroup
import com.painzone.domain.session.StartSessionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class SessionRepositoryImplTest {

    private lateinit var store: FakeSessionStore
    private lateinit var sessionDao: FakeWorkoutSessionDao
    private lateinit var snapshotDao: FakeSessionExerciseSnapshotDao
    private lateinit var loggedSetDao: FakeLoggedSetDao
    private lateinit var dayDao: FakePlannedDayDao
    private lateinit var planDao: FakeTrainingPlanDao
    private lateinit var itemDao: FakePlannedExerciseDao
    private lateinit var exerciseDao: FakeExerciseDao
    private lateinit var repo: SessionRepositoryImpl

    @Before
    fun setUp() {
        store = FakeSessionStore()
        sessionDao = FakeWorkoutSessionDao(store)
        snapshotDao = FakeSessionExerciseSnapshotDao(store)
        loggedSetDao = FakeLoggedSetDao(store)
        dayDao = FakePlannedDayDao()
        planDao = FakeTrainingPlanDao()
        itemDao = FakePlannedExerciseDao()
        exerciseDao = FakeExerciseDao()
        repo = SessionRepositoryImpl(
            sessionDao, snapshotDao, loggedSetDao, dayDao, planDao, itemDao, exerciseDao,
        )
    }

    // Seeds a plan with one day and the given exercises (name, group, targetReps, rest).
    private fun seedPlanDay(
        planName: String = "PPL",
        dayName: String = "Push",
        exercises: List<ExSpec> = emptyList(),
    ): Long {
        val planId = planDao.seed(planName)
        val dayId = dayDao.seed(planId, dayName)
        exercises.forEachIndexed { index, spec ->
            val exId = spec.exerciseId ?: exerciseDao.seed(spec.name, spec.group)
            itemDao.seed(dayId, exId, index, spec.targetReps, spec.rest)
        }
        return dayId
    }

    private data class ExSpec(
        val name: String = "Bench",
        val group: MuscleGroup = MuscleGroup.Chest,
        val targetReps: List<Int> = listOf(8, 8, 8),
        val rest: Int? = 90,
        val exerciseId: Long? = null,
    )

    @Test
    fun `start creates session with plan and day name snapshots`() = runTest {
        val dayId = seedPlanDay("Upper/Lower", "Upper", listOf(ExSpec()))

        val result = repo.start(dayId)

        assertTrue(result is StartSessionResult.Success)
        val id = (result as StartSessionResult.Success).sessionId
        val session = store.sessions[id]!!
        assertEquals("Upper/Lower", session.planNameSnapshot)
        assertEquals("Upper", session.dayNameSnapshot)
        assertEquals(dayId, session.plannedDayId)
        assertNull(session.finishedAt)
    }

    @Test
    fun `start snapshots each planned exercise with plan params and contiguous order`() = runTest {
        val dayId = seedPlanDay(
            exercises = listOf(
                ExSpec("Bench", MuscleGroup.Chest, listOf(8, 8, 8), 90),
                ExSpec("Press", MuscleGroup.Shoulders, listOf(10, 10), 60),
            ),
        )

        val id = (repo.start(dayId) as StartSessionResult.Success).sessionId
        val snaps = store.snapshots.values.filter { it.sessionId == id }.sortedBy { it.order }

        assertEquals(2, snaps.size)
        assertEquals(listOf(0, 1), snaps.map { it.order })
        assertEquals("Bench", snaps[0].exerciseNameSnapshot)
        assertEquals(MuscleGroup.Chest, snaps[0].muscleGroupSnapshot)
        assertEquals(listOf(8, 8, 8), snaps[0].plannedTargetReps)
        assertEquals(90, snaps[0].plannedRestSeconds)
        assertEquals("Press", snaps[1].exerciseNameSnapshot)
        assertEquals(listOf(10, 10), snaps[1].plannedTargetReps)
        assertEquals(60, snaps[1].plannedRestSeconds)
    }

    @Test
    fun `start returns DayNotFound for unknown day`() = runTest {
        assertEquals(StartSessionResult.DayNotFound, repo.start(999L))
    }

    @Test
    fun `start returns EmptyDay when day has no exercises`() = runTest {
        val dayId = seedPlanDay(exercises = emptyList())
        assertEquals(StartSessionResult.EmptyDay, repo.start(dayId))
    }

    @Test
    fun `start returns AlreadyInProgress when an in-progress session exists`() = runTest {
        val dayId = seedPlanDay(exercises = listOf(ExSpec()))
        repo.start(dayId)

        assertEquals(StartSessionResult.AlreadyInProgress, repo.start(dayId))
    }

    @Test
    fun `start snapshots a soft-deleted exercise by freezing its name`() = runTest {
        val exId = exerciseDao.seed("Curl", MuscleGroup.Biceps)
        exerciseDao.softDelete(exId)
        val dayId = seedPlanDay(
            exercises = listOf(ExSpec(exerciseId = exId, targetReps = listOf(12), rest = null)),
        )

        val id = (repo.start(dayId) as StartSessionResult.Success).sessionId
        val snap = store.snapshots.values.first { it.sessionId == id }
        assertEquals("Curl", snap.exerciseNameSnapshot)
        assertEquals(exId, snap.exerciseId)
    }

    @Test
    fun `observeInProgress emits started session then null after no in-progress`() = runTest {
        val dayId = seedPlanDay(exercises = listOf(ExSpec()))

        assertNull(repo.observeInProgress().first())

        val id = (repo.start(dayId) as StartSessionResult.Success).sessionId
        assertEquals(id, repo.observeInProgress().first()!!.id)
        assertEquals(id, repo.getInProgress()!!.id)
    }

    @Test
    fun `getSessionDetail returns session with ordered snapshots and logged sets`() = runTest {
        val dayId = seedPlanDay(
            exercises = listOf(
                ExSpec("Bench", MuscleGroup.Chest, listOf(8), 90),
                ExSpec("Press", MuscleGroup.Shoulders, listOf(10), 60),
            ),
        )
        val id = (repo.start(dayId) as StartSessionResult.Success).sessionId
        val firstSnap = store.snapshots.values.first { it.sessionId == id && it.order == 0 }
        store.seedSet(firstSnap.id, order = 1, reps = 8, weight = 60.0)

        val detail = repo.getSessionDetail(id)!!
        assertEquals(2, detail.exercises.size)
        assertEquals(listOf(0, 1), detail.exercises.map { it.snapshot.order })
        assertEquals(1, detail.exercises[0].loggedSets.size)
        assertEquals(60.0, detail.exercises[0].loggedSets[0].weight, 0.0)
        assertTrue(detail.exercises[1].loggedSets.isEmpty())
    }

    @Test
    fun `lastWeightForExercise returns null without history and latest weight with history`() = runTest {
        val exId = exerciseDao.seed("Squat", MuscleGroup.Legs)
        assertNull(repo.lastWeightForExercise(exId))

        // Two sets logged at different times; latest completedAt wins.
        val snapId = store.seedSnapshot(sessionId = 1L, exerciseId = exId)
        store.seedSet(snapId, order = 1, reps = 5, weight = 100.0, at = Instant.ofEpochSecond(100))
        store.seedSet(snapId, order = 2, reps = 5, weight = 110.0, at = Instant.ofEpochSecond(200))

        assertEquals(110.0, repo.lastWeightForExercise(exId)!!, 0.0)
    }

    @Test
    fun `lastSessionSetsForExercise returns the prior session's ordered set list and ignores current`() = runTest {
        val exId = exerciseDao.seed("Squat", MuscleGroup.Legs)
        // Prior session (id 1): three series, ascending weight.
        val priorSnap = store.seedSnapshot(sessionId = 1L, exerciseId = exId)
        store.seedSet(priorSnap, order = 1, reps = 10, weight = 80.0, at = Instant.ofEpochSecond(100))
        store.seedSet(priorSnap, order = 2, reps = 10, weight = 85.0, at = Instant.ofEpochSecond(200))
        store.seedSet(priorSnap, order = 3, reps = 8, weight = 90.0, at = Instant.ofEpochSecond(300))
        // Current session (id 2): newer sets that must be excluded.
        val currentSnap = store.seedSnapshot(sessionId = 2L, exerciseId = exId)
        store.seedSet(currentSnap, order = 1, reps = 9, weight = 95.0, at = Instant.ofEpochSecond(400))

        val sets = repo.lastSessionSetsForExercise(exId, excludingSessionId = 2L)
        // Ordered by series (order_in_exercise), so the per-series preview lines up by index.
        assertEquals(listOf(80.0, 85.0, 90.0), sets.map { it.weight })
        assertEquals(listOf(10, 10, 8), sets.map { it.reps })
    }

    @Test
    fun `lastSessionSetsForExercise picks the most recent prior session`() = runTest {
        val exId = exerciseDao.seed("Squat", MuscleGroup.Legs)
        // Older prior session (id 1).
        val oldSnap = store.seedSnapshot(sessionId = 1L, exerciseId = exId)
        store.seedSet(oldSnap, order = 1, reps = 5, weight = 70.0, at = Instant.ofEpochSecond(100))
        // Newer prior session (id 2) — its sets should be returned.
        val newSnap = store.seedSnapshot(sessionId = 2L, exerciseId = exId)
        store.seedSet(newSnap, order = 1, reps = 10, weight = 80.0, at = Instant.ofEpochSecond(500))
        store.seedSet(newSnap, order = 2, reps = 10, weight = 82.5, at = Instant.ofEpochSecond(600))

        val sets = repo.lastSessionSetsForExercise(exId, excludingSessionId = 99L)
        assertEquals(listOf(80.0, 82.5), sets.map { it.weight })
    }

    @Test
    fun `log first set records no rest before it`() = runTest {
        val snapId = store.seedSnapshot(sessionId = 1L, exerciseId = 1L)

        val setId = repo.log(snapId, reps = 10, weight = 60.0, rpe = null)

        val set = store.sets[setId]!!
        assertEquals(1, set.order)
        assertNull(set.restBeforeSeconds)
    }

    @Test
    fun `log subsequent set records actual rest since the previous set`() = runTest {
        val snapId = store.seedSnapshot(sessionId = 1L, exerciseId = 1L)
        // Previous set finished two minutes ago; rest before the new set should be ~120s.
        store.seedSet(snapId, order = 1, reps = 10, weight = 60.0, at = Instant.now().minusSeconds(120))

        val setId = repo.log(snapId, reps = 9, weight = 62.5, rpe = null)

        val set = store.sets[setId]!!
        assertEquals(2, set.order)
        assertTrue("expected ~120s rest, got ${set.restBeforeSeconds}", set.restBeforeSeconds!! >= 115)
    }

    @Test
    fun `lastSessionSetsForExercise is empty when only the current session has sets`() = runTest {
        val exId = exerciseDao.seed("Squat", MuscleGroup.Legs)
        val currentSnap = store.seedSnapshot(sessionId = 2L, exerciseId = exId)
        store.seedSet(currentSnap, order = 1, reps = 8, weight = 120.0, at = Instant.ofEpochSecond(300))

        assertTrue(repo.lastSessionSetsForExercise(exId, excludingSessionId = 2L).isEmpty())
    }
}

// Shared in-memory store mimicking the three session tables + reactive bump.
private class FakeSessionStore {
    var nextSessionId = 1L
    var nextSnapshotId = 1L
    var nextSetId = 1L
    val sessions = mutableMapOf<Long, WorkoutSessionEntity>()
    val snapshots = mutableMapOf<Long, SessionExerciseSnapshotEntity>()
    val sets = mutableMapOf<Long, LoggedSetEntity>()
    val version = MutableStateFlow(0)

    fun bump() {
        version.value += 1
    }

    fun seedSnapshot(sessionId: Long, exerciseId: Long): Long {
        val id = nextSnapshotId++
        snapshots[id] = SessionExerciseSnapshotEntity(
            id = id,
            sessionId = sessionId,
            exerciseId = exerciseId,
            exerciseNameSnapshot = "Seed",
            muscleGroupSnapshot = MuscleGroup.Chest,
            order = 0,
            plannedTargetReps = listOf(8),
            plannedRestSeconds = null,
        )
        bump()
        return id
    }

    fun seedSet(
        snapshotId: Long,
        order: Int,
        reps: Int,
        weight: Double,
        at: Instant = Instant.now(),
    ): Long {
        val id = nextSetId++
        sets[id] = LoggedSetEntity(
            id = id,
            sessionExerciseSnapshotId = snapshotId,
            order = order,
            reps = reps,
            weight = weight,
            rpe = null,
            completedAt = at,
        )
        bump()
        return id
    }

    fun detail(sessionId: Long): SessionWithDetail? {
        val session = sessions[sessionId] ?: return null
        val snaps = snapshots.values.filter { it.sessionId == sessionId }.map { snap ->
            SnapshotWithLoggedSets(
                snapshot = snap,
                loggedSets = sets.values.filter { it.sessionExerciseSnapshotId == snap.id },
            )
        }
        return SessionWithDetail(session = session, snapshots = snaps)
    }
}

private class FakeWorkoutSessionDao(private val store: FakeSessionStore) : WorkoutSessionDao {
    override suspend fun insert(entity: WorkoutSessionEntity): Long {
        val id = store.nextSessionId++
        store.sessions[id] = entity.copy(id = id)
        store.bump()
        return id
    }

    override suspend fun insertSnapshots(
        entities: List<SessionExerciseSnapshotEntity>,
    ): List<Long> = entities.map { snap ->
        val id = store.nextSnapshotId++
        store.snapshots[id] = snap.copy(id = id)
        store.bump()
        id
    }

    override suspend fun update(entity: WorkoutSessionEntity) {
        store.sessions[entity.id] = entity
        store.bump()
    }

    override suspend fun deleteById(id: Long) {
        store.sessions.remove(id)
        store.bump()
    }

    override suspend fun getById(id: Long): WorkoutSessionEntity? = store.sessions[id]

    override suspend fun getInProgress(): WorkoutSessionEntity? =
        store.sessions.values.firstOrNull { it.finishedAt == null }

    override fun observeInProgress(): Flow<WorkoutSessionEntity?> =
        store.version.map { store.sessions.values.firstOrNull { s -> s.finishedAt == null } }

    override fun observeCompleted(): Flow<List<WorkoutSessionEntity>> =
        store.version.map { store.sessions.values.filter { it.finishedAt != null } }

    override suspend fun getWithSnapshots(id: Long): SessionWithSnapshots? =
        throw NotImplementedError()

    override fun observeWithSnapshots(id: Long): Flow<SessionWithSnapshots?> =
        throw NotImplementedError()

    override suspend fun getWithDetail(id: Long): SessionWithDetail? = store.detail(id)

    override fun observeWithDetail(id: Long): Flow<SessionWithDetail?> =
        store.version.map { store.detail(id) }
}

private class FakeSessionExerciseSnapshotDao(
    private val store: FakeSessionStore,
) : SessionExerciseSnapshotDao {
    override suspend fun insert(entity: SessionExerciseSnapshotEntity): Long =
        throw NotImplementedError()

    override suspend fun insertAll(entities: List<SessionExerciseSnapshotEntity>): List<Long> =
        throw NotImplementedError()

    override suspend fun getById(id: Long): SessionExerciseSnapshotEntity? = store.snapshots[id]

    override fun observeBySessionId(sessionId: Long): Flow<List<SessionExerciseSnapshotEntity>> =
        throw NotImplementedError()

    override suspend fun getWithLoggedSets(id: Long): SnapshotWithLoggedSets? =
        throw NotImplementedError()

    override fun observeWithLoggedSetsBySessionId(
        sessionId: Long,
    ): Flow<List<SnapshotWithLoggedSets>> = throw NotImplementedError()
}

private class FakeLoggedSetDao(private val store: FakeSessionStore) : LoggedSetDao {
    override suspend fun insert(entity: LoggedSetEntity): Long {
        val id = store.nextSetId++
        store.sets[id] = entity.copy(id = id)
        store.bump()
        return id
    }

    override suspend fun update(entity: LoggedSetEntity) = throw NotImplementedError()
    override suspend fun deleteById(id: Long) = throw NotImplementedError()
    override suspend fun getById(id: Long): LoggedSetEntity? = store.sets[id]

    override fun observeBySnapshotId(snapshotId: Long): Flow<List<LoggedSetEntity>> =
        throw NotImplementedError()

    override suspend fun getBySnapshotId(snapshotId: Long): List<LoggedSetEntity> =
        store.sets.values
            .filter { it.sessionExerciseSnapshotId == snapshotId }
            .sortedBy { it.order }

    override suspend fun maxOrderInSnapshot(snapshotId: Long): Int? =
        store.sets.values.filter { it.sessionExerciseSnapshotId == snapshotId }
            .maxOfOrNull { it.order }

    override suspend fun lastCompletedAtInSnapshot(snapshotId: Long): Instant? =
        store.sets.values.filter { it.sessionExerciseSnapshotId == snapshotId }
            .maxByOrNull { it.order }
            ?.completedAt

    override suspend fun lastWeightForExercise(exerciseId: Long): Double? {
        val snapIds = store.snapshots.values.filter { it.exerciseId == exerciseId }.map { it.id }
        return store.sets.values
            .filter { it.sessionExerciseSnapshotId in snapIds }
            .maxByOrNull { it.completedAt }
            ?.weight
    }

    override suspend fun lastSessionSnapshotIdForExercise(
        exerciseId: Long,
        excludingSessionId: Long,
    ): Long? {
        val snapIds = store.snapshots.values
            .filter { it.exerciseId == exerciseId && it.sessionId != excludingSessionId }
            .map { it.id }
        return store.sets.values
            .filter { it.sessionExerciseSnapshotId in snapIds }
            .maxByOrNull { it.completedAt }
            ?.sessionExerciseSnapshotId
    }

    override suspend fun updateOrder(id: Long, order: Int) = throw NotImplementedError()
}

private class FakePlannedDayDao : PlannedDayDao {
    private var nextId = 1L
    private val store = mutableMapOf<Long, PlannedDayEntity>()

    fun seed(planId: Long, name: String): Long {
        val id = nextId++
        store[id] = PlannedDayEntity(id = id, trainingPlanId = planId, name = name, order = 0)
        return id
    }

    override suspend fun insert(entity: PlannedDayEntity): Long = throw NotImplementedError()
    override suspend fun update(entity: PlannedDayEntity) = throw NotImplementedError()
    override suspend fun deleteById(id: Long) = throw NotImplementedError()
    override suspend fun getById(id: Long): PlannedDayEntity? = store[id]
    override fun observeByPlanId(planId: Long): Flow<List<PlannedDayEntity>> =
        throw NotImplementedError()
    override suspend fun maxOrderInPlan(planId: Long): Int? = throw NotImplementedError()
    override suspend fun findInPlanByName(planId: Long, name: String): PlannedDayEntity? =
        throw NotImplementedError()
}

private class FakeTrainingPlanDao : TrainingPlanDao {
    private var nextId = 1L
    private val store = mutableMapOf<Long, TrainingPlanEntity>()

    fun seed(name: String): Long {
        val id = nextId++
        store[id] = TrainingPlanEntity(
            id = id,
            name = name,
            isActive = false,
            createdAt = Instant.now(),
        )
        return id
    }

    override suspend fun insert(entity: TrainingPlanEntity): Long = throw NotImplementedError()
    override suspend fun update(entity: TrainingPlanEntity) = throw NotImplementedError()
    override suspend fun deleteById(id: Long) = throw NotImplementedError()
    override suspend fun getById(id: Long): TrainingPlanEntity? = store[id]
    override fun observeAll(): Flow<List<TrainingPlanEntity>> = throw NotImplementedError()
    override fun observeSummaries(): Flow<List<PlanSummaryRow>> = throw NotImplementedError()
    override fun observeActive(): Flow<TrainingPlanEntity?> = throw NotImplementedError()
    override suspend fun findByName(name: String): TrainingPlanEntity? = throw NotImplementedError()
    override suspend fun deactivateAll() = throw NotImplementedError()
    override suspend fun deactivateById(id: Long) = throw NotImplementedError()
    override suspend fun activateById(id: Long) = throw NotImplementedError()
    override suspend fun getWithDays(id: Long): PlanWithDays? = throw NotImplementedError()
    override fun observeWithDays(id: Long): Flow<PlanWithDays?> = throw NotImplementedError()
}

private class FakePlannedExerciseDao : PlannedExerciseDao {
    private var nextId = 1L
    private val store = mutableMapOf<Long, PlannedExerciseEntity>()

    fun seed(dayId: Long, exerciseId: Long, order: Int, targetReps: List<Int>, rest: Int?): Long {
        val id = nextId++
        store[id] = PlannedExerciseEntity(
            id = id,
            plannedDayId = dayId,
            exerciseId = exerciseId,
            order = order,
            targetReps = targetReps,
            restSeconds = rest,
        )
        return id
    }

    override suspend fun insert(entity: PlannedExerciseEntity): Long = throw NotImplementedError()
    override suspend fun update(entity: PlannedExerciseEntity) = throw NotImplementedError()
    override suspend fun deleteById(id: Long) = throw NotImplementedError()
    override suspend fun getById(id: Long): PlannedExerciseEntity? = store[id]
    override fun observeByDayId(dayId: Long): Flow<List<PlannedExerciseEntity>> =
        throw NotImplementedError()
    override suspend fun getByDayId(dayId: Long): List<PlannedExerciseEntity> =
        store.values.filter { it.plannedDayId == dayId }.sortedBy { it.order }
    override suspend fun maxOrderInDay(dayId: Long): Int? = throw NotImplementedError()
    override suspend fun updateOrder(id: Long, order: Int) = throw NotImplementedError()
    override suspend fun countDistinctPlansForExercise(exerciseId: Long): Int =
        throw NotImplementedError()
}

private class FakeExerciseDao : ExerciseDao {
    private var nextId = 1L
    private val store = mutableMapOf<Long, ExerciseEntity>()

    fun seed(name: String, muscleGroup: MuscleGroup): Long {
        val id = nextId++
        store[id] = ExerciseEntity(
            id = id,
            name = name,
            muscleGroup = muscleGroup,
            createdAt = Instant.now(),
            deletedAt = null,
        )
        return id
    }

    fun softDelete(id: Long) {
        store[id]?.let { store[id] = it.copy(deletedAt = Instant.now()) }
    }

    override suspend fun insert(entity: ExerciseEntity): Long = throw NotImplementedError()
    override suspend fun update(entity: ExerciseEntity) = throw NotImplementedError()
    override fun observeActive(): Flow<List<ExerciseEntity>> = throw NotImplementedError()
    override suspend fun getById(id: Long): ExerciseEntity? = store[id]
    override suspend fun findActiveByName(name: String): ExerciseEntity? = throw NotImplementedError()
}
