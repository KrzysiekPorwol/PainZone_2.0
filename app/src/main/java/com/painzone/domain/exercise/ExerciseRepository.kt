package com.painzone.domain.exercise

import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun observeActive(): Flow<List<Exercise>>
    fun observeById(id: Long): Flow<Exercise?>
    // Ids of soft-deleted exercises — feeds the S14 "usunięte" marker per session exercise.
    fun observeDeletedIds(): Flow<Set<Long>>
    suspend fun getById(id: Long): Exercise?
    suspend fun getUsageCount(id: Long): ExerciseUsage
    // Distinct plan names referencing the exercise. Non-empty → delete is blocked.
    suspend fun plansUsing(id: Long): List<String>
    suspend fun create(name: String, muscleGroup: MuscleGroup): CreateResult
    suspend fun rename(id: Long, newName: String): RenameResult
    suspend fun softDelete(id: Long): SoftDeleteResult
}

data class ExerciseUsage(
    val plansCount: Int,
    val sessionsCount: Int,
)

sealed interface CreateResult {
    data class Success(val id: Long) : CreateResult
    data object DuplicateName : CreateResult
}

sealed interface RenameResult {
    data object Success : RenameResult
    data object DuplicateName : RenameResult
    data object NotFound : RenameResult
}

sealed interface SoftDeleteResult {
    data object Success : SoftDeleteResult
    data object NotFound : SoftDeleteResult
    data object AlreadyDeleted : SoftDeleteResult
}