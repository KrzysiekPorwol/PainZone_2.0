package com.painzone.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.painzone.domain.exercise.CreateResult
import com.painzone.domain.exercise.ExerciseRepository
import com.painzone.domain.exercise.MuscleGroup
import com.painzone.domain.exercise.RenameResult
import com.painzone.domain.exercise.SoftDeleteResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    private val _deleteDialogState = MutableStateFlow<DeleteDialogState>(DeleteDialogState.Hidden)
    val deleteDialogState: StateFlow<DeleteDialogState> = _deleteDialogState.asStateFlow()

    private val _editDialogState = MutableStateFlow<EditDialogState>(EditDialogState.Hidden)
    val editDialogState: StateFlow<EditDialogState> = _editDialogState.asStateFlow()

    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    suspend fun addExercise(name: String, muscleGroup: MuscleGroup): CreateResult =
        repository.create(name, muscleGroup)

    fun requestEdit(exerciseId: Long) {
        viewModelScope.launch {
            val exercise = repository.getById(exerciseId) ?: return@launch
            val usage = repository.getUsageCount(exerciseId)
            _editDialogState.value = EditDialogState.Visible(
                exerciseId = exercise.id,
                initialName = exercise.name,
                muscleGroup = exercise.muscleGroup,
                usage = usage,
            )
        }
    }

    fun cancelEdit() {
        _editDialogState.value = EditDialogState.Hidden
    }

    suspend fun renameExercise(id: Long, newName: String): RenameResult {
        val result = repository.rename(id, newName)
        if (result is RenameResult.Success) {
            _editDialogState.value = EditDialogState.Hidden
            _snackbarEvents.emit("Zapisano")
        }
        return result
    }

    fun requestDelete(exerciseId: Long) {
        viewModelScope.launch {
            val exercise = repository.getById(exerciseId) ?: return@launch
            val planNames = repository.plansUsing(exerciseId)
            _deleteDialogState.value = if (planNames.isNotEmpty()) {
                DeleteDialogState.Blocked(
                    exerciseName = exercise.name,
                    planNames = planNames,
                )
            } else {
                DeleteDialogState.Confirm(
                    exerciseId = exercise.id,
                    exerciseName = exercise.name,
                    usage = repository.getUsageCount(exerciseId),
                )
            }
        }
    }

    fun cancelDelete() {
        _deleteDialogState.value = DeleteDialogState.Hidden
    }

    fun confirmDelete() {
        val current = _deleteDialogState.value as? DeleteDialogState.Confirm ?: return
        viewModelScope.launch {
            val result = repository.softDelete(current.exerciseId)
            _deleteDialogState.value = DeleteDialogState.Hidden
            if (result is SoftDeleteResult.Success) {
                _snackbarEvents.emit("Usunięto")
            }
        }
    }
}