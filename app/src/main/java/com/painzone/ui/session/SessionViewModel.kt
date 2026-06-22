package com.painzone.ui.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.painzone.domain.session.SessionRepository
import com.painzone.ui.navigation.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SessionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SessionRepository,
) : ViewModel() {

    private val sessionId: Long = savedStateHandle.toRoute<Session>().sessionId

    // Which exercise is in focus. Kept in the VM so it survives recomposition and
    // jump/advance actions stay consistent with the rendered session graph.
    private val activeIndex = MutableStateFlow(0)

    val uiState: StateFlow<SessionUiState> =
        combine(repository.observeSessionDetail(sessionId), activeIndex) { detail, index ->
            if (detail == null) {
                SessionUiState.NotFound
            } else {
                val exercises = detail.exercises
                    .sortedBy { it.snapshot.order }
                    .map { ex ->
                        SessionExerciseUi(
                            snapshotId = ex.snapshot.id,
                            name = ex.snapshot.exerciseNameSnapshot,
                            muscleGroup = ex.snapshot.muscleGroupSnapshot,
                            plannedTargetReps = ex.snapshot.plannedTargetReps,
                            plannedRestSeconds = ex.snapshot.plannedRestSeconds,
                            loggedSetCount = ex.loggedSets.size,
                        )
                    }
                if (exercises.isEmpty()) {
                    SessionUiState.NotFound
                } else {
                    SessionUiState.Content(
                        planName = detail.session.planNameSnapshot,
                        dayName = detail.session.dayNameSnapshot,
                        exercises = exercises,
                        activeIndex = index.coerceIn(0, exercises.lastIndex),
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SessionUiState.Loading,
        )

    // One-shot: session finished, screen should leave S9.
    private val _finished = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val finished: SharedFlow<Unit> = _finished.asSharedFlow()

    fun finishSession() {
        viewModelScope.launch {
            repository.finish(sessionId)
            _finished.emit(Unit)
        }
    }

    fun selectExercise(index: Int) {
        activeIndex.value = index
    }

    fun nextExercise() {
        val content = uiState.value as? SessionUiState.Content ?: return
        if (content.hasNext) activeIndex.value = content.activeIndex + 1
    }

    fun previousExercise() {
        val content = uiState.value as? SessionUiState.Content ?: return
        if (content.hasPrevious) activeIndex.value = content.activeIndex - 1
    }
}
