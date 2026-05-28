package com.painzone.ui.library

import com.painzone.domain.exercise.Exercise
import com.painzone.domain.exercise.ExerciseUsage

sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data object Empty : LibraryUiState
    data class Content(val items: List<Exercise>) : LibraryUiState
}

sealed interface DeleteDialogState {
    data object Hidden : DeleteDialogState
    data class Visible(
        val exerciseId: Long,
        val exerciseName: String,
        val usage: ExerciseUsage,
    ) : DeleteDialogState
}