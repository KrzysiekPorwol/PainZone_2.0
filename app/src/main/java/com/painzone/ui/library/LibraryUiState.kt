package com.painzone.ui.library

import com.painzone.domain.exercise.Exercise

sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data object Empty : LibraryUiState
    data class Content(val items: List<Exercise>) : LibraryUiState
}