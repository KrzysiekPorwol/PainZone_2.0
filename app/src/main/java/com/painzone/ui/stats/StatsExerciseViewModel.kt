package com.painzone.ui.stats

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.painzone.domain.exercise.ExerciseRepository
import com.painzone.domain.stats.StatsPeriod
import com.painzone.domain.stats.StatsRepository
import com.painzone.ui.navigation.StatsExercise
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StatsExerciseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: StatsRepository,
    exerciseRepository: ExerciseRepository,
) : ViewModel() {

    private val exerciseId: Long = savedStateHandle.toRoute<StatsExercise>().exerciseId

    private val _selectedPeriod = MutableStateFlow(StatsPeriod.DEFAULT)
    val selectedPeriod: StateFlow<StatsPeriod> = _selectedPeriod.asStateFlow()

    // Soft-deleted exercise → read-only marker (M4.5). History still renders via the snapshot
    // JOIN; this only drives the banner. Frozen name/group come from the route (set at M4.3).
    val isDeleted: StateFlow<Boolean> = exerciseRepository.observeById(exerciseId)
        .map { it != null && !it.isActive }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    // Re-query on every filter change; the window's `now` is fixed per subscription (see repo).
    val uiState: StateFlow<StatsUiState> = _selectedPeriod
        .flatMapLatest { period -> repository.observeSets(exerciseId, period) }
        .map { sets ->
            if (sets.isEmpty()) {
                StatsUiState.Empty
            } else {
                StatsUiState.Content(
                    best = sets.toBestSetUi(LocalDate.now()),
                    sessions = sets.toSessionUi(),
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatsUiState.Loading,
        )

    fun selectPeriod(period: StatsPeriod) {
        _selectedPeriod.value = period
    }
}
