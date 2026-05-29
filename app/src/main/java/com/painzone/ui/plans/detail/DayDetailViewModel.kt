package com.painzone.ui.plans.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.painzone.domain.exercise.ExerciseRepository
import com.painzone.domain.plan.PlanRepository
import com.painzone.ui.navigation.DayDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class DayDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val planRepository: PlanRepository,
    exerciseRepository: ExerciseRepository,
) : ViewModel() {

    private val dayId: Long = savedStateHandle.toRoute<DayDetail>().dayId

    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    val uiState: StateFlow<DayDetailUiState> =
        combine(
            planRepository.observeExercisesByDay(dayId),
            exerciseRepository.observeActive(),
        ) { planned, activeExercises ->
            val nameById = activeExercises.associate { it.id to it.name }
            val rows = planned.map { pe ->
                val name = nameById[pe.exerciseId]
                PlannedExerciseRow(
                    plannedExerciseId = pe.id,
                    exerciseId = pe.exerciseId,
                    name = name ?: "Ćwiczenie usunięte",
                    isDeleted = name == null,
                    targetReps = pe.targetReps,
                    restSeconds = pe.restSeconds,
                )
            }
            DayDetailUiState.Content(rows) as DayDetailUiState
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DayDetailUiState.Loading,
        )

    // Suspends so the params sheet can dismiss only after the write lands.
    suspend fun updateParams(plannedExerciseId: Long, targetReps: List<Int>, restSeconds: Int?) {
        planRepository.updateExerciseParams(plannedExerciseId, targetReps, restSeconds)
    }

    fun removeExercise(plannedExerciseId: Long, name: String) {
        viewModelScope.launch {
            planRepository.removeExercise(plannedExerciseId)
            _snackbarEvents.emit("Usunięto: $name")
        }
    }
}