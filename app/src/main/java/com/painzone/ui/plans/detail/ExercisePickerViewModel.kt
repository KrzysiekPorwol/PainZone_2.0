package com.painzone.ui.plans.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.painzone.domain.exercise.CreateResult
import com.painzone.domain.exercise.ExerciseRepository
import com.painzone.domain.exercise.MuscleGroup
import com.painzone.domain.plan.AddExerciseResult
import com.painzone.domain.plan.PlanRepository
import com.painzone.ui.library.labelPl
import com.painzone.ui.navigation.ExercisePicker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ExercisePickerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val planRepository: PlanRepository,
    private val exerciseRepository: ExerciseRepository,
) : ViewModel() {

    private val dayId: Long = savedStateHandle.toRoute<ExercisePicker>().dayId

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    fun onQueryChange(value: String) {
        _query.value = value
    }

    val uiState: StateFlow<ExercisePickerUiState> =
        combine(exerciseRepository.observeActive(), _query) { all, q ->
            if (all.isEmpty()) {
                ExercisePickerUiState.Empty
            } else {
                val term = q.trim()
                val filtered =
                    if (term.isEmpty()) all
                    else all.filter { it.name.contains(term, ignoreCase = true) }
                // observeActive already sorts by name; group order follows MuscleGroup enum.
                val sections = filtered
                    .groupBy { it.muscleGroup }
                    .entries
                    .sortedBy { it.key.ordinal }
                    .map { (group: MuscleGroup, list) ->
                        PickerSection(
                            groupLabel = group.labelPl,
                            items = list.map { PickerItem(it.id, it.name) },
                        )
                    }
                ExercisePickerUiState.Content(sections, noResults = sections.isEmpty())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ExercisePickerUiState.Loading,
        )

    suspend fun addExercise(
        exerciseId: Long,
        targetReps: List<Int>,
        restSeconds: Int?,
    ): AddExerciseResult = planRepository.addExercise(dayId, exerciseId, targetReps, restSeconds)

    suspend fun createExercise(name: String, muscleGroup: MuscleGroup): CreateResult =
        exerciseRepository.create(name, muscleGroup)
}