package com.painzone.data.exercise

import com.painzone.domain.exercise.CreateResult
import com.painzone.domain.exercise.MuscleGroup
import com.painzone.domain.exercise.RenameResult
import com.painzone.domain.exercise.SoftDeleteResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExerciseRepositoryImplTest {

    private lateinit var dao: FakeExerciseDao
    private lateinit var repo: ExerciseRepositoryImpl

    @Before
    fun setUp() {
        dao = FakeExerciseDao()
        repo = ExerciseRepositoryImpl(dao)
    }

    @Test
    fun `create returns Success with positive id and persists active record`() = runTest {
        val result = repo.create("Bench press", MuscleGroup.Chest)

        assertTrue(result is CreateResult.Success)
        val id = (result as CreateResult.Success).id
        assertTrue(id > 0L)
        val stored = dao.getById(id)
        assertNotNull(stored)
        assertEquals("Bench press", stored!!.name)
        assertEquals(MuscleGroup.Chest, stored.muscleGroup)
        assertNull(stored.deletedAt)
    }

    @Test
    fun `create trims name before persisting`() = runTest {
        val result = repo.create("  Squat  ", MuscleGroup.Legs) as CreateResult.Success
        assertEquals("Squat", dao.getById(result.id)!!.name)
    }

    @Test
    fun `create returns DuplicateName when active record with same name exists`() = runTest {
        repo.create("Deadlift", MuscleGroup.Back)

        val result = repo.create("Deadlift", MuscleGroup.Back)

        assertEquals(CreateResult.DuplicateName, result)
    }

    @Test
    fun `create succeeds when only a soft-deleted record has the same name`() = runTest {
        val first = repo.create("Row", MuscleGroup.Back) as CreateResult.Success
        repo.softDelete(first.id)

        val result = repo.create("Row", MuscleGroup.Back)

        assertTrue(result is CreateResult.Success)
        assertTrue((result as CreateResult.Success).id != first.id)
    }

    @Test
    fun `rename returns NotFound for unknown id`() = runTest {
        assertEquals(RenameResult.NotFound, repo.rename(id = 999L, newName = "Anything"))
    }

    @Test
    fun `rename returns Success no-op when newName equals current name after trim`() = runTest {
        val id = (repo.create("Curl", MuscleGroup.Biceps) as CreateResult.Success).id

        val result = repo.rename(id, "  Curl  ")

        assertEquals(RenameResult.Success, result)
        assertEquals("Curl", dao.getById(id)!!.name)
    }

    @Test
    fun `rename returns DuplicateName when another active record already uses newName`() = runTest {
        val a = (repo.create("Press", MuscleGroup.Shoulders) as CreateResult.Success).id
        repo.create("Lateral raise", MuscleGroup.Shoulders)

        val result = repo.rename(a, "Lateral raise")

        assertEquals(RenameResult.DuplicateName, result)
        assertEquals("Press", dao.getById(a)!!.name)
    }

    @Test
    fun `rename persists the new trimmed name`() = runTest {
        val id = (repo.create("Plank", MuscleGroup.Abs) as CreateResult.Success).id

        val result = repo.rename(id, "  Side plank  ")

        assertEquals(RenameResult.Success, result)
        assertEquals("Side plank", dao.getById(id)!!.name)
    }

    @Test
    fun `softDelete returns NotFound for unknown id`() = runTest {
        assertEquals(SoftDeleteResult.NotFound, repo.softDelete(123L))
    }

    @Test
    fun `softDelete sets deletedAt and returns Success`() = runTest {
        val id = (repo.create("Dip", MuscleGroup.Triceps) as CreateResult.Success).id

        val result = repo.softDelete(id)

        assertEquals(SoftDeleteResult.Success, result)
        assertNotNull(dao.getById(id)!!.deletedAt)
    }

    @Test
    fun `softDelete returns AlreadyDeleted for already soft-deleted record`() = runTest {
        val id = (repo.create("Pulldown", MuscleGroup.Back) as CreateResult.Success).id
        repo.softDelete(id)

        val result = repo.softDelete(id)

        assertEquals(SoftDeleteResult.AlreadyDeleted, result)
    }

    @Test
    fun `rename propagates new name to observeActive (library view)`() = runTest {
        // M1.7 propagation contract: edit name → biblioteka i przyszłe sesje widzą nową nazwę.
        // Historical snapshot immutability test deferred to M3.2 (SessionExerciseSnapshot powstanie wtedy).
        val id = (repo.create("Bench", MuscleGroup.Chest) as CreateResult.Success).id

        val result = repo.rename(id, "Incline bench")

        assertEquals(RenameResult.Success, result)
        val emitted = repo.observeActive().first()
        assertEquals(1, emitted.size)
        assertEquals("Incline bench", emitted.first().name)
        assertEquals(id, emitted.first().id)
    }

    @Test
    fun `observeActive emits only records with null deletedAt`() = runTest {
        val active = (repo.create("Hip thrust", MuscleGroup.Legs) as CreateResult.Success).id
        val deleted = (repo.create("Calf raise", MuscleGroup.Legs) as CreateResult.Success).id
        repo.softDelete(deleted)

        val emitted = repo.observeActive().first()

        assertEquals(1, emitted.size)
        assertEquals(active, emitted.first().id)
    }
}

private class FakeExerciseDao : ExerciseDao {

    private var nextId = 1L
    private val store = mutableMapOf<Long, ExerciseEntity>()
    private val flow = MutableStateFlow<List<ExerciseEntity>>(emptyList())

    override suspend fun insert(entity: ExerciseEntity): Long {
        val id = nextId++
        val persisted = entity.copy(id = id)
        store[id] = persisted
        publish()
        return id
    }

    override suspend fun update(entity: ExerciseEntity) {
        store[entity.id] = entity
        publish()
    }

    override fun observeActive(): Flow<List<ExerciseEntity>> =
        flow.map { list ->
            list.filter { it.deletedAt == null }
                .sortedBy { it.name.lowercase() }
        }

    override suspend fun getById(id: Long): ExerciseEntity? = store[id]

    override suspend fun findActiveByName(name: String): ExerciseEntity? =
        store.values.firstOrNull { it.name == name && it.deletedAt == null }

    private fun publish() {
        flow.value = store.values.toList()
    }
}