package com.painzone.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.painzone.domain.exercise.CreateResult
import com.painzone.domain.exercise.ExerciseRepository
import com.painzone.domain.exercise.MuscleGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: ExerciseRepository,
) : ViewModel() {

    val uiState: StateFlow<LibraryUiState> = repository.observeActive()
        .map { exercises ->
            if (exercises.isEmpty()) LibraryUiState.Empty
            else LibraryUiState.Content(exercises)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LibraryUiState.Loading,
        )

    suspend fun addExercise(name: String, muscleGroup: MuscleGroup): CreateResult =
        repository.create(name, muscleGroup)
}