package com.painzone.ui.library

import com.painzone.domain.exercise.Exercise
import com.painzone.domain.exercise.ExerciseUsage
import com.painzone.domain.exercise.MuscleGroup

sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data object Empty : LibraryUiState
    data class Content(val items: List<Exercise>) : LibraryUiState
}

sealed interface DeleteDialogState {
    data object Hidden : DeleteDialogState

    // Exercise is in no plan → soft delete allowed (history, if any, kept read-only).
    data class Confirm(
        val exerciseId: Long,
        val exerciseName: String,
        val usage: ExerciseUsage,
    ) : DeleteDialogState

    // Exercise is referenced by ≥1 plan → delete blocked until removed from those plans.
    data class Blocked(
        val exerciseName: String,
        val planNames: List<String>,
    ) : DeleteDialogState
}

sealed interface EditDialogState {
    data object Hidden : EditDialogState
    data class Visible(
        val exerciseId: Long,
        val initialName: String,
        val muscleGroup: MuscleGroup,
        val usage: ExerciseUsage,
    ) : EditDialogState
}