package com.painzone.data.plan

import com.painzone.data.exercise.ExerciseDao
import com.painzone.data.exercise.ExerciseEntity
import com.painzone.data.plan.relation.DayWithExercises
import com.painzone.data.plan.relation.PlanSummaryRow
import com.painzone.data.plan.relation.PlanWithDays
import com.painzone.domain.exercise.MuscleGroup
import com.painzone.domain.plan.ActivatePlanResult
import com.painzone.domain.plan.AddDayResult
import com.painzone.domain.plan.AddExerciseResult
import com.painzone.domain.plan.CreatePlanResult
import com.painzone.domain.plan.DeletePlanResult
import com.painzone.domain.plan.DeleteResult
import com.painzone.domain.plan.RenameDayResult
import com.painzone.domain.plan.RenamePlanResult
import com.painzone.domain.plan.UpdateResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class PlanRepositoryImplTest {

    private lateinit var planDao: FakeTrainingPlanDao
    private lateinit var dayDao: FakePlannedDayDao
    private lateinit var itemDao: FakePlannedExerciseDao
    private lateinit var exerciseDao: FakeExerciseDaoForPlanTest
    private lateinit var repo: PlanRepositoryImpl

    @Before
    fun setUp() {
        planDao = FakeTrainingPlanDao()
        dayDao = FakePlannedDayDao()
        itemDao = FakePlannedExerciseDao()
        exerciseDao = FakeExerciseDaoForPlanTest()
        // Wire cascade via test-only callbacks so fakes mimic FK ON DELETE CASCADE.
        planDao.onDelete = { id -> dayDao.deleteByPlanId(id) }
        dayDao.onDelete = { id -> itemDao.deleteByDayId(id) }
        repo = PlanRepositoryImpl(planDao, dayDao, itemDao, exerciseDao)
    }

    @Test
    fun `create returns Success with positive id and trims name`() = runTest {
        val result = repo.create("  Push pull  ")

        assertTrue(result is CreatePlanResult.Success)
        val id = (result as CreatePlanResult.Success).id
        assertEquals("Push pull", planDao.getById(id)!!.name)
        assertFalse(planDao.getById(id)!!.isActive)
    }

    @Test
    fun `create returns DuplicateName for existing global plan name`() = runTest {
        repo.create("PPL")
        assertEquals(CreatePlanResult.DuplicateName, repo.create("PPL"))
    }

    @Test
    fun `rename returns NotFound for unknown id`() = runTest {
        assertEquals(RenamePlanResult.NotFound, repo.rename(999L, "x"))
    }

    @Test
    fun `rename returns DuplicateName when another plan uses newName`() = runTest {
        val a = (repo.create("A") as CreatePlanResult.Success).id
        repo.create("B")

        assertEquals(RenamePlanResult.DuplicateName, repo.rename(a, "B"))
        assertEquals("A", planDao.getById(a)!!.name)
    }

    @Test
    fun `rename persists trimmed new name`() = runTest {
        val id = (repo.create("Old") as CreatePlanResult.Success).id

        assertEquals(RenamePlanResult.Success, repo.rename(id, "  New  "))
        assertEquals("New", planDao.getById(id)!!.name)
    }

    @Test
    fun `setActive deactivates previously active plan and activates target`() = runTest {
        val a = (repo.create("A") as CreatePlanResult.Success).id
        val b = (repo.create("B") as CreatePlanResult.Success).id

        assertEquals(ActivatePlanResult.Success, repo.setActive(a))
        assertTrue(planDao.getById(a)!!.isActive)

        assertEquals(ActivatePlanResult.Success, repo.setActive(b))
        assertFalse("a should be deactivated after b becomes active", planDao.getById(a)!!.isActive)
        assertTrue(planDao.getById(b)!!.isActive)
    }

    @Test
    fun `setActive returns NotFound for unknown id`() = runTest {
        assertEquals(ActivatePlanResult.NotFound, repo.setActive(123L))
    }

    @Test
    fun `delete plan cascades to days and exercises`() = runTest {
        val planId = (repo.create("P") as CreatePlanResult.Success).id
        val dayId = (repo.addDay(planId, "Day 1") as AddDayResult.Success).id
        val exId = (exerciseDao.seed("Bench", MuscleGroup.Chest))
        val itemId = (repo.addExercise(dayId, exId, listOf(8, 8, 8), 90)
            as AddExerciseResult.Success).id

        assertEquals(DeletePlanResult.Success, repo.delete(planId))
        assertNull(planDao.getById(planId))
        assertNull(dayDao.getById(dayId))
        assertNull(itemDao.getById(itemId))
    }

    @Test
    fun `addDay returns PlanNotFound for unknown planId`() = runTest {
        assertEquals(AddDayResult.PlanNotFound, repo.addDay(42L, "Day"))
    }

    @Test
    fun `addDay assigns order = max + 1 starting from 0`() = runTest {
        val planId = (repo.create("P") as CreatePlanResult.Success).id

        val d0 = (repo.addDay(planId, "First") as AddDayResult.Success).id
        val d1 = (repo.addDay(planId, "Second") as AddDayResult.Success).id
        val d2 = (repo.addDay(planId, "Third") as AddDayResult.Success).id

        assertEquals(0, dayDao.getById(d0)!!.order)
        assertEquals(1, dayDao.getById(d1)!!.order)
        assertEquals(2, dayDao.getById(d2)!!.order)
    }

    @Test
    fun `addDay rejects duplicate name within same plan`() = runTest {
        val planId = (repo.create("P") as CreatePlanResult.Success).id
        repo.addDay(planId, "Push")

        assertEquals(AddDayResult.DuplicateName, repo.addDay(planId, "Push"))
    }

    @Test
    fun `addDay allows same name across different plans`() = runTest {
        val p1 = (repo.create("P1") as CreatePlanResult.Success).id
        val p2 = (repo.create("P2") as CreatePlanResult.Success).id
        repo.addDay(p1, "Push")

        val result = repo.addDay(p2, "Push")
        assertTrue(result is AddDayResult.Success)
    }

    @Test
    fun `renameDay rejects duplicate within same plan`() = runTest {
        val planId = (repo.create("P") as CreatePlanResult.Success).id
        val push = (repo.addDay(planId, "Push") as AddDayResult.Success).id
        repo.addDay(planId, "Pull")

        assertEquals(RenameDayResult.DuplicateName, repo.renameDay(push, "Pull"))
        assertEquals("Push", dayDao.getById(push)!!.name)
    }

    @Test
    fun `deleteDay cascades to its exercises`() = runTest {
        val planId = (repo.create("P") as CreatePlanResult.Success).id
        val dayId = (repo.addDay(planId, "Day") as AddDayResult.Success).id
        val exId = exerciseDao.seed("Squat", MuscleGroup.Legs)
        val itemId = (repo.addExercise(dayId, exId, listOf(5, 5, 5, 5, 5), 180)
            as AddExerciseResult.Success).id

        assertEquals(DeleteResult.Success, repo.deleteDay(dayId))
        assertNull(itemDao.getById(itemId))
    }

    @Test
    fun `addExercise returns ExerciseNotFound when exercise id is unknown`() = runTest {
        val planId = (repo.create("P") as CreatePlanResult.Success).id
        val dayId = (repo.addDay(planId, "Day") as AddDayResult.Success).id

        assertEquals(
            AddExerciseResult.ExerciseNotFound,
            repo.addExercise(dayId, 999L, listOf(10), null),
        )
    }

    @Test
    fun `addExercise returns ExerciseDeleted for soft-deleted exercise`() = runTest {
        val planId = (repo.create("P") as CreatePlanResult.Success).id
        val dayId = (repo.addDay(planId, "Day") as AddDayResult.Success).id
        val exId = exerciseDao.seed("Curl", MuscleGroup.Biceps)
        exerciseDao.softDelete(exId)

        assertEquals(
            AddExerciseResult.ExerciseDeleted,
            repo.addExercise(dayId, exId, listOf(10), null),
        )
    }

    @Test
    fun `addExercise returns DayNotFound when day is unknown`() = runTest {
        val exId = exerciseDao.seed("Press", MuscleGroup.Shoulders)

        assertEquals(
            AddExerciseResult.DayNotFound,
            repo.addExercise(404L, exId, listOf(10), null),
        )
    }

    @Test
    fun `addExercise assigns order = max + 1 per day`() = runTest {
        val planId = (repo.create("P") as CreatePlanResult.Success).id
        val dayId = (repo.addDay(planId, "Day") as AddDayResult.Success).id
        val exId = exerciseDao.seed("Bench", MuscleGroup.Chest)

        val i0 = (repo.addExercise(dayId, exId, listOf(8), null) as AddExerciseResult.Success).id
        val i1 = (repo.addExercise(dayId, exId, listOf(8), null) as AddExerciseResult.Success).id

        assertEquals(0, itemDao.getById(i0)!!.order)
        assertEquals(1, itemDao.getById(i1)!!.order)
    }

    @Test
    fun `updateExerciseParams persists new targetReps and restSeconds`() = runTest {
        val planId = (repo.create("P") as CreatePlanResult.Success).id
        val dayId = (repo.addDay(planId, "Day") as AddDayResult.Success).id
        val exId = exerciseDao.seed("Row", MuscleGroup.Back)
        val itemId = (repo.addExercise(dayId, exId, listOf(10), 60) as AddExerciseResult.Success).id

        assertEquals(
            UpdateResult.Success,
            repo.updateExerciseParams(itemId, listOf(10, 9, 8), 120),
        )
        val stored = itemDao.getById(itemId)!!
        assertEquals(listOf(10, 9, 8), stored.targetReps)
        assertEquals(120, stored.restSeconds)
    }

    @Test
    fun `observeAll emits plans through the flow`() = runTest {
        repo.create("A")
        repo.create("B")

        val emitted = repo.observeAll().first()

        assertEquals(2, emitted.size)
    }

    @Test
    fun `observeActive emits null when no plan is active`() = runTest {
        repo.create("A")

        assertNull(repo.observeActive().first())
    }

    @Test
    fun `observeActive emits the active plan after setActive`() = runTest {
        val id = (repo.create("A") as CreatePlanResult.Success).id
        repo.setActive(id)

        val active = repo.observeActive().first()
        assertNotNull(active)
        assertEquals(id, active!!.id)
    }
}

private class FakeTrainingPlanDao : TrainingPlanDao {
    private var nextId = 1L
    private val store = mutableMapOf<Long, TrainingPlanEntity>()
    private val flow = MutableStateFlow<List<TrainingPlanEntity>>(emptyList())

    var onDelete: (suspend (Long) -> Unit)? = null

    override suspend fun insert(entity: TrainingPlanEntity): Long {
        val id = nextId++
        store[id] = entity.copy(id = id)
        publish()
        return id
    }

    override suspend fun update(entity: TrainingPlanEntity) {
        store[entity.id] = entity
        publish()
    }

    override suspend fun deleteById(id: Long) {
        store.remove(id)
        publish()
        onDelete?.invoke(id)
    }

    override suspend fun getById(id: Long): TrainingPlanEntity? = store[id]

    override fun observeAll(): Flow<List<TrainingPlanEntity>> =
        flow.map { it.sortedByDescending { p -> p.createdAt } }

    override fun observeSummaries(): Flow<List<PlanSummaryRow>> =
        flow.map { list ->
            list.sortedByDescending { p -> p.createdAt }
                .map { PlanSummaryRow(plan = it, dayCount = 0) }
        }

    override fun observeActive(): Flow<TrainingPlanEntity?> =
        flow.map { list -> list.firstOrNull { it.isActive } }

    override suspend fun findByName(name: String): TrainingPlanEntity? =
        store.values.firstOrNull { it.name == name }

    override suspend fun deactivateAll() {
        store.values.toList().forEach { store[it.id] = it.copy(isActive = false) }
        publish()
    }

    override suspend fun activateById(id: Long) {
        store[id]?.let { store[id] = it.copy(isActive = true) }
        publish()
    }

    override suspend fun activateExclusive(id: Long) {
        deactivateAll()
        activateById(id)
    }

    override suspend fun getWithDays(id: Long): PlanWithDays? = null
    override fun observeWithDays(id: Long): Flow<PlanWithDays?> = MutableStateFlow(null)

    private fun publish() {
        flow.value = store.values.toList()
    }
}

private class FakePlannedDayDao : PlannedDayDao {
    private var nextId = 1L
    private val store = mutableMapOf<Long, PlannedDayEntity>()
    private val flow = MutableStateFlow<List<PlannedDayEntity>>(emptyList())

    var onDelete: (suspend (Long) -> Unit)? = null

    override suspend fun insert(entity: PlannedDayEntity): Long {
        val id = nextId++
        store[id] = entity.copy(id = id)
        publish()
        return id
    }

    override suspend fun update(entity: PlannedDayEntity) {
        store[entity.id] = entity
        publish()
    }

    override suspend fun deleteById(id: Long) {
        store.remove(id)
        publish()
        onDelete?.invoke(id)
    }

    override suspend fun getById(id: Long): PlannedDayEntity? = store[id]

    override fun observeByPlanId(planId: Long): Flow<List<PlannedDayEntity>> =
        flow.map { list -> list.filter { it.trainingPlanId == planId }.sortedBy { it.order } }

    override suspend fun maxOrderInPlan(planId: Long): Int? =
        store.values.filter { it.trainingPlanId == planId }.maxOfOrNull { it.order }

    override suspend fun findInPlanByName(planId: Long, name: String): PlannedDayEntity? =
        store.values.firstOrNull { it.trainingPlanId == planId && it.name == name }

    suspend fun deleteByPlanId(planId: Long) {
        val toRemove = store.values.filter { it.trainingPlanId == planId }.map { it.id }
        toRemove.forEach { deleteById(it) }
    }

    private fun publish() {
        flow.value = store.values.toList()
    }
}

private class FakePlannedExerciseDao : PlannedExerciseDao {
    private var nextId = 1L
    private val store = mutableMapOf<Long, PlannedExerciseEntity>()
    private val flow = MutableStateFlow<List<PlannedExerciseEntity>>(emptyList())

    override suspend fun insert(entity: PlannedExerciseEntity): Long {
        val id = nextId++
        store[id] = entity.copy(id = id)
        publish()
        return id
    }

    override suspend fun update(entity: PlannedExerciseEntity) {
        store[entity.id] = entity
        publish()
    }

    override suspend fun deleteById(id: Long) {
        store.remove(id)
        publish()
    }

    override suspend fun getById(id: Long): PlannedExerciseEntity? = store[id]

    override fun observeByDayId(dayId: Long): Flow<List<PlannedExerciseEntity>> =
        flow.map { list -> list.filter { it.plannedDayId == dayId }.sortedBy { it.order } }

    override suspend fun maxOrderInDay(dayId: Long): Int? =
        store.values.filter { it.plannedDayId == dayId }.maxOfOrNull { it.order }

    override suspend fun countDistinctPlansForExercise(exerciseId: Long): Int = 0

    suspend fun deleteByDayId(dayId: Long) {
        val toRemove = store.values.filter { it.plannedDayId == dayId }.map { it.id }
        toRemove.forEach { deleteById(it) }
    }

    private fun publish() {
        flow.value = store.values.toList()
    }
}

private class FakeExerciseDaoForPlanTest : ExerciseDao {
    private var nextId = 1L
    private val store = mutableMapOf<Long, ExerciseEntity>()
    private val flow = MutableStateFlow<List<ExerciseEntity>>(emptyList())

    fun seed(name: String, muscleGroup: MuscleGroup): Long {
        val id = nextId++
        store[id] = ExerciseEntity(
            id = id,
            name = name,
            muscleGroup = muscleGroup,
            createdAt = Instant.now(),
            deletedAt = null,
        )
        publish()
        return id
    }

    fun softDelete(id: Long) {
        store[id]?.let { store[id] = it.copy(deletedAt = Instant.now()) }
        publish()
    }

    override suspend fun insert(entity: ExerciseEntity): Long {
        val id = nextId++
        store[id] = entity.copy(id = id)
        publish()
        return id
    }

    override suspend fun update(entity: ExerciseEntity) {
        store[entity.id] = entity
        publish()
    }

    override fun observeActive(): Flow<List<ExerciseEntity>> =
        flow.map { list -> list.filter { it.deletedAt == null } }

    override suspend fun getById(id: Long): ExerciseEntity? = store[id]

    override suspend fun findActiveByName(name: String): ExerciseEntity? =
        store.values.firstOrNull { it.name == name && it.deletedAt == null }

    private fun publish() {
        flow.value = store.values.toList()
    }
}