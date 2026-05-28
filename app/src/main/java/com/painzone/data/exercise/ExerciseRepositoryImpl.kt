package com.painzone.data.exercise

import com.painzone.data.plan.PlannedExerciseDao
import com.painzone.domain.exercise.CreateResult
import com.painzone.domain.exercise.Exercise
import com.painzone.domain.exercise.ExerciseRepository
import com.painzone.domain.exercise.ExerciseUsage
import com.painzone.domain.exercise.MuscleGroup
import com.painzone.domain.exercise.RenameResult
import com.painzone.domain.exercise.SoftDeleteResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepositoryImpl @Inject constructor(
    private val dao: ExerciseDao,
    private val plannedExerciseDao: PlannedExerciseDao,
) : ExerciseRepository {

    override fun observeActive(): Flow<List<Exercise>> =
        dao.observeActive().map { list -> list.map(ExerciseEntity::toDomain) }

    override suspend fun getById(id: Long): Exercise? =
        dao.getById(id)?.toDomain()

    // sessionsCount stays 0 until M3.3 (WorkoutSession layer exists).
    override suspend fun getUsageCount(id: Long): ExerciseUsage =
        ExerciseUsage(
            plansCount = plannedExerciseDao.countDistinctPlansForExercise(id),
            sessionsCount = 0,
        )

    override suspend fun create(name: String, muscleGroup: MuscleGroup): CreateResult {
        val trimmed = name.trim()
        if (dao.findActiveByName(trimmed) != null) return CreateResult.DuplicateName
        val newId = dao.insert(
            Exercise.create(trimmed, muscleGroup, Instant.now()).toEntity(),
        )
        return CreateResult.Success(newId)
    }

    override suspend fun rename(id: Long, newName: String): RenameResult {
        val current = dao.getById(id)?.toDomain() ?: return RenameResult.NotFound
        val trimmed = newName.trim()
        if (trimmed == current.name) return RenameResult.Success
        val conflict = dao.findActiveByName(trimmed)
        if (conflict != null && conflict.id != id) return RenameResult.DuplicateName
        dao.update(current.rename(trimmed).toEntity())
        return RenameResult.Success
    }

    override suspend fun softDelete(id: Long): SoftDeleteResult {
        val current = dao.getById(id)?.toDomain() ?: return SoftDeleteResult.NotFound
        if (!current.isActive) return SoftDeleteResult.AlreadyDeleted
        dao.update(current.softDelete(Instant.now()).toEntity())
        return SoftDeleteResult.Success
    }
}