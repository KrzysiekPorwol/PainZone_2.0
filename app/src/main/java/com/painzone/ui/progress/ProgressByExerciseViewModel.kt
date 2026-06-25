package com.painzone.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.painzone.domain.exercise.Exercise
import com.painzone.domain.exercise.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ProgressByExerciseViewModel @Inject constructor(
    repository: ExerciseRepository,
) : ViewModel() {

    // "Po ćwiczeniu" lists active exercises as entry points to per-exercise stats (S10).
    // "ostatnio Xd" and hiding exercises without history are deferred (need a session-aggregate
    // query) — S10 shows its own empty state for an exercise with no logs in the window.
    val uiState: StateFlow<ProgressByExerciseUiState> = repository.observeActive()
        .map { exercises ->
            if (exercises.isEmpty()) ProgressByExerciseUiState.Empty
            else ProgressByExerciseUiState.Content(exercises)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProgressByExerciseUiState.Loading,
        )
}

sealed interface ProgressByExerciseUiState {
    data object Loading : ProgressByExerciseUiState
    data object Empty : ProgressByExerciseUiState
    data class Content(val exercises: List<Exercise>) : ProgressByExerciseUiState
}
