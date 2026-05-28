package com.painzone.domain.exercise

import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun observeActive(): Flow<List<Exercise>>
    suspend fun getById(id: Long): Exercise?
    suspend fun getUsageCount(id: Long): ExerciseUsage
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