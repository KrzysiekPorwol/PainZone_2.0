package com.painzone.data.plan

import com.painzone.data.exercise.ExerciseDao
import com.painzone.domain.plan.ActivatePlanResult
import com.painzone.domain.plan.AddDayResult
import com.painzone.domain.plan.AddExerciseResult
import com.painzone.domain.plan.CreatePlanResult
import com.painzone.domain.plan.DayWithExercises
import com.painzone.domain.plan.DeletePlanResult
import com.painzone.domain.plan.DeleteResult
import com.painzone.domain.plan.PlanRepository
import com.painzone.domain.plan.PlanSummary
import com.painzone.domain.plan.PlanWithDays
import com.painzone.domain.plan.PlannedDay
import com.painzone.domain.plan.PlannedExercise
import com.painzone.domain.plan.RenameDayResult
import com.painzone.domain.plan.RenamePlanResult
import com.painzone.domain.plan.ReorderResult
import com.painzone.domain.plan.TrainingPlan
import com.painzone.domain.plan.UpdateResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import com.painzone.data.plan.relation.PlanWithDays as PlanWithDaysRelation

@Singleton
class PlanRepositoryImpl @Inject constructor(
    private val planDao: TrainingPlanDao,
    private val dayDao: PlannedDayDao,
    private val itemDao: PlannedExerciseDao,
    private val exerciseDao: ExerciseDao,
) : PlanRepository {

    override fun observeAll(): Flow<List<TrainingPlan>> =
        planDao.observeAll().map { list -> list.map(TrainingPlanEntity::toDomain) }

    override fun observeSummaries(): Flow<List<PlanSummary>> =
        planDao.observeSummaries().map { rows ->
            rows.map { row ->
                PlanSummary(
                    id = row.plan.id,
                    name = row.plan.name,
                    isActive = row.plan.isActive,
                    dayCount = row.dayCount,
                )
            }
        }

    override fun observeActive(): Flow<TrainingPlan?> =
        planDao.observeActive().map { it?.toDomain() }

    override fun observePlanWithDays(id: Long): Flow<PlanWithDays?> =
        planDao.observeWithDays(id).map { it?.toDomain() }

    override suspend fun getById(id: Long): TrainingPlan? =
        planDao.getById(id)?.toDomain()

    override suspend fun create(name: String): CreatePlanResult {
        val trimmed = name.trim()
        if (planDao.findByName(trimmed) != null) return CreatePlanResult.DuplicateName
        val id = planDao.insert(
            TrainingPlan.create(trimmed, Instant.now()).toEntity(),
        )
        return CreatePlanResult.Success(id)
    }

    override suspend fun rename(id: Long, newName: String): RenamePlanResult {
        val current = planDao.getById(id)?.toDomain() ?: return RenamePlanResult.NotFound
        val trimmed = newName.trim()
        if (trimmed == current.name) return RenamePlanResult.Success
        val conflict = planDao.findByName(trimmed)
        if (conflict != null && conflict.id != id) return RenamePlanResult.DuplicateName
        planDao.update(current.rename(trimmed).toEntity())
        return RenamePlanResult.Success
    }

    override suspend fun setActive(id: Long): ActivatePlanResult {
        planDao.getById(id) ?: return ActivatePlanResult.NotFound
        planDao.activateExclusive(id)
        return ActivatePlanResult.Success
    }

    override suspend fun deactivate(id: Long): ActivatePlanResult {
        planDao.getById(id) ?: return ActivatePlanResult.NotFound
        planDao.deactivateById(id)
        return ActivatePlanResult.Success
    }

    override suspend fun delete(id: Long): DeletePlanResult {
        planDao.getById(id) ?: return DeletePlanResult.NotFound
        planDao.deleteById(id)
        return DeletePlanResult.Success
    }

    override fun observeDaysByPlan(planId: Long): Flow<List<PlannedDay>> =
        dayDao.observeByPlanId(planId).map { list -> list.map(PlannedDayEntity::toDomain) }

    override suspend fun addDay(planId: Long, name: String): AddDayResult {
        planDao.getById(planId) ?: return AddDayResult.PlanNotFound
        val trimmed = name.trim()
        if (dayDao.findInPlanByName(planId, trimmed) != null) return AddDayResult.DuplicateName
        val order = (dayDao.maxOrderInPlan(planId) ?: -1) + 1
        val id = dayDao.insert(
            PlannedDay.create(planId, trimmed, order).toEntity(),
        )
        return AddDayResult.Success(id)
    }

    override suspend fun renameDay(id: Long, newName: String): RenameDayResult {
        val current = dayDao.getById(id)?.toDomain() ?: return RenameDayResult.NotFound
        val trimmed = newName.trim()
        if (trimmed == current.name) return RenameDayResult.Success
        val conflict = dayDao.findInPlanByName(current.trainingPlanId, trimmed)
        if (conflict != null && conflict.id != id) return RenameDayResult.DuplicateName
        dayDao.update(current.rename(trimmed).toEntity())
        return RenameDayResult.Success
    }

    override suspend fun reorderDay(id: Long, newOrder: Int): ReorderResult {
        val current = dayDao.getById(id)?.toDomain() ?: return ReorderResult.NotFound
        dayDao.update(current.reorder(newOrder).toEntity())
        return ReorderResult.Success
    }

    override suspend fun deleteDay(id: Long): DeleteResult {
        dayDao.getById(id) ?: return DeleteResult.NotFound
        dayDao.deleteById(id)
        return DeleteResult.Success
    }

    override fun observeExercisesByDay(dayId: Long): Flow<List<PlannedExercise>> =
        itemDao.observeByDayId(dayId).map { list -> list.map(PlannedExerciseEntity::toDomain) }

    override suspend fun addExercise(
        dayId: Long,
        exerciseId: Long,
        targetReps: List<Int>,
        restSeconds: Int?,
    ): AddExerciseResult {
        dayDao.getById(dayId) ?: return AddExerciseResult.DayNotFound
        val exercise = exerciseDao.getById(exerciseId) ?: return AddExerciseResult.ExerciseNotFound
        if (exercise.deletedAt != null) return AddExerciseResult.ExerciseDeleted
        val order = (itemDao.maxOrderInDay(dayId) ?: -1) + 1
        val id = itemDao.insert(
            PlannedExercise.create(
                plannedDayId = dayId,
                exerciseId = exerciseId,
                targetReps = targetReps,
                restSeconds = restSeconds,
                order = order,
            ).toEntity(),
        )
        return AddExerciseResult.Success(id)
    }

    override suspend fun updateExerciseParams(
        id: Long,
        targetReps: List<Int>,
        restSeconds: Int?,
    ): UpdateResult {
        val current = itemDao.getById(id)?.toDomain() ?: return UpdateResult.NotFound
        itemDao.update(current.updateParams(targetReps, restSeconds).toEntity())
        return UpdateResult.Success
    }

    override suspend fun reorderExercises(dayId: Long, orderedIds: List<Long>): ReorderResult {
        // Reject if any id is missing or belongs to a different day — keeps order_in_day
        // consistent within the day and guards against stale UI ids.
        for (id in orderedIds) {
            val item = itemDao.getById(id) ?: return ReorderResult.NotFound
            if (item.plannedDayId != dayId) return ReorderResult.NotFound
        }
        itemDao.reorderInDay(orderedIds)
        return ReorderResult.Success
    }

    override suspend fun removeExercise(id: Long): DeleteResult {
        itemDao.getById(id) ?: return DeleteResult.NotFound
        itemDao.deleteById(id)
        return DeleteResult.Success
    }

    private fun PlanWithDaysRelation.toDomain(): PlanWithDays = PlanWithDays(
        plan = plan.toDomain(),
        days = days
            .sortedBy { it.day.order }
            .map { dwe ->
                DayWithExercises(
                    day = dwe.day.toDomain(),
                    exercises = dwe.exercises
                        .sortedBy { it.order }
                        .map(PlannedExerciseEntity::toDomain),
                )
            },
    )
}