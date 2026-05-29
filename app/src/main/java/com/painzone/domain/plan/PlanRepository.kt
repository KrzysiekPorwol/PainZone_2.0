package com.painzone.domain.plan

import kotlinx.coroutines.flow.Flow

interface PlanRepository {

    // TrainingPlan
    fun observeAll(): Flow<List<TrainingPlan>>
    fun observeSummaries(): Flow<List<PlanSummary>>
    fun observeActive(): Flow<TrainingPlan?>
    fun observePlanWithDays(id: Long): Flow<PlanWithDays?>
    suspend fun getById(id: Long): TrainingPlan?
    suspend fun create(name: String): CreatePlanResult
    suspend fun rename(id: Long, newName: String): RenamePlanResult
    suspend fun setActive(id: Long): ActivatePlanResult
    suspend fun delete(id: Long): DeletePlanResult

    // PlannedDay
    fun observeDaysByPlan(planId: Long): Flow<List<PlannedDay>>
    suspend fun addDay(planId: Long, name: String): AddDayResult
    suspend fun renameDay(id: Long, newName: String): RenameDayResult
    suspend fun reorderDay(id: Long, newOrder: Int): ReorderResult
    suspend fun deleteDay(id: Long): DeleteResult

    // PlannedExercise
    fun observeExercisesByDay(dayId: Long): Flow<List<PlannedExercise>>
    suspend fun addExercise(
        dayId: Long,
        exerciseId: Long,
        targetReps: List<Int>,
        restSeconds: Int?,
    ): AddExerciseResult
    suspend fun updateExerciseParams(
        id: Long,
        targetReps: List<Int>,
        restSeconds: Int?,
    ): UpdateResult
    suspend fun reorderExercise(id: Long, newOrder: Int): ReorderResult
    suspend fun removeExercise(id: Long): DeleteResult
}

data class PlanWithDays(
    val plan: TrainingPlan,
    val days: List<DayWithExercises>,
)

data class DayWithExercises(
    val day: PlannedDay,
    val exercises: List<PlannedExercise>,
)

sealed interface CreatePlanResult {
    data class Success(val id: Long) : CreatePlanResult
    data object DuplicateName : CreatePlanResult
}

sealed interface RenamePlanResult {
    data object Success : RenamePlanResult
    data object DuplicateName : RenamePlanResult
    data object NotFound : RenamePlanResult
}

sealed interface ActivatePlanResult {
    data object Success : ActivatePlanResult
    data object NotFound : ActivatePlanResult
}

sealed interface DeletePlanResult {
    data object Success : DeletePlanResult
    data object NotFound : DeletePlanResult
}

sealed interface AddDayResult {
    data class Success(val id: Long) : AddDayResult
    data object DuplicateName : AddDayResult
    data object PlanNotFound : AddDayResult
}

sealed interface RenameDayResult {
    data object Success : RenameDayResult
    data object DuplicateName : RenameDayResult
    data object NotFound : RenameDayResult
}

sealed interface AddExerciseResult {
    data class Success(val id: Long) : AddExerciseResult
    data object DayNotFound : AddExerciseResult
    data object ExerciseNotFound : AddExerciseResult
    data object ExerciseDeleted : AddExerciseResult
}

sealed interface UpdateResult {
    data object Success : UpdateResult
    data object NotFound : UpdateResult
}

sealed interface ReorderResult {
    data object Success : ReorderResult
    data object NotFound : ReorderResult
}

sealed interface DeleteResult {
    data object Success : DeleteResult
    data object NotFound : DeleteResult
}