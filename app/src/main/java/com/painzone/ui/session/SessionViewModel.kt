package com.painzone.ui.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.painzone.domain.session.Rpe
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
                            exerciseId = ex.snapshot.exerciseId,
                            name = ex.snapshot.exerciseNameSnapshot,
                            muscleGroup = ex.snapshot.muscleGroupSnapshot,
                            plannedTargetReps = ex.snapshot.plannedTargetReps,
                            plannedRestSeconds = ex.snapshot.plannedRestSeconds,
                            loggedSets = ex.loggedSets.map { set ->
                                LoggedSetUi(set.id, set.order, set.reps, set.weight, set.rpe)
                            },
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

    private val _input = MutableStateFlow(SetInputUi())
    val input: StateFlow<SetInputUi> = _input.asStateFlow()

    // One-shot: input was reset, screen should re-focus the reps field.
    private val _focusReps = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val focusReps: SharedFlow<Unit> = _focusReps.asSharedFlow()

    // One-shot: session finished, screen should leave S9.
    private val _finished = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val finished: SharedFlow<Unit> = _finished.asSharedFlow()

    // Snapshot the input was last (re)built for — guards against clobbering a user's typing
    // on every DB emission while still re-prefilling when the active exercise changes.
    private var inputInitializedFor: Long? = null

    init {
        viewModelScope.launch {
            uiState.collect { state ->
                val content = state as? SessionUiState.Content ?: return@collect
                val active = content.activeExercise
                if (active.snapshotId != inputInitializedFor) {
                    inputInitializedFor = active.snapshotId
                    _input.value = freshInput(active)
                }
            }
        }
    }

    // ---- Input editing ----

    fun updateReps(value: String) = _input.update { it.copy(reps = value.filter(Char::isDigit)) }

    fun updateWeight(value: String) =
        _input.update { it.copy(weight = value.filter { c -> c.isDigit() || c == '.' }) }

    fun incrementReps() = _input.update {
        it.copy(reps = (((it.reps.toIntOrNull() ?: 0) + 1).coerceAtLeast(1)).toString())
    }

    fun decrementReps() = _input.update {
        it.copy(reps = (((it.reps.toIntOrNull() ?: 1) - 1).coerceAtLeast(1)).toString())
    }

    fun incrementWeight() = _input.update {
        it.copy(weight = formatWeight(((it.weight.toDoubleOrNull() ?: 0.0) + WEIGHT_STEP).coerceAtLeast(0.0)))
    }

    fun decrementWeight() = _input.update {
        it.copy(weight = formatWeight(((it.weight.toDoubleOrNull() ?: 0.0) - WEIGHT_STEP).coerceAtLeast(0.0)))
    }

    fun selectRpe(rpe: Rpe) = _input.update { it.copy(rpe = if (it.rpe == rpe) null else rpe) }

    // Loads the freshest set into the input row for inline overwrite.
    fun editSet(setId: Long) {
        val exercise = (uiState.value as? SessionUiState.Content)?.activeExercise ?: return
        val set = exercise.loggedSets.firstOrNull { it.id == setId } ?: return
        _input.value = SetInputUi(
            reps = set.reps.toString(),
            weight = formatWeight(set.weight),
            rpe = set.rpe,
            editingSetId = set.id,
        )
        _focusReps.tryEmit(Unit)
    }

    fun cancelEdit() {
        val exercise = (uiState.value as? SessionUiState.Content)?.activeExercise ?: return
        viewModelScope.launch { _input.value = freshInput(exercise) }
    }

    fun saveSet() {
        val current = _input.value
        val reps = current.reps.toIntOrNull()?.takeIf { it >= 1 } ?: return
        val weight = (current.weight.toDoubleOrNull() ?: 0.0).takeIf { it >= 0.0 } ?: return
        val exercise = (uiState.value as? SessionUiState.Content)?.activeExercise ?: return
        val editingId = current.editingSetId
        viewModelScope.launch {
            if (editingId != null) {
                repository.edit(editingId, reps, weight, current.rpe)
                // After an overwrite we go back to appending the next not-yet-logged set.
                _input.value = nextInput(exercise, exercise.loggedSetCount, weight)
            } else {
                repository.log(exercise.snapshotId, reps, weight, current.rpe)
                // One more set is now logged; prefill for the set after it, keeping the weight.
                _input.value = nextInput(exercise, exercise.loggedSetCount + 1, weight)
            }
            _focusReps.tryEmit(Unit)
        }
    }

    // ---- Exercise navigation (input re-prefills via the uiState collector) ----

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

    fun finishSession() {
        viewModelScope.launch {
            repository.finish(sessionId)
            _finished.emit(Unit)
        }
    }

    // Input for the active exercise's next not-yet-logged set: reps from plan target,
    // weight carried from the last set in this exercise or, failing that, the last session.
    private suspend fun freshInput(exercise: SessionExerciseUi): SetInputUi {
        val carried = exercise.loggedSets.maxByOrNull { it.order }?.weight
            ?: repository.lastWeightForExercise(exercise.exerciseId)
        return nextInput(exercise, exercise.loggedSetCount, carried)
    }

    // setsLogged = how many sets are already done; picks the matching plan target for reps.
    private fun nextInput(exercise: SessionExerciseUi, setsLogged: Int, weight: Double?): SetInputUi {
        val targetReps = exercise.plannedTargetReps.getOrElse(setsLogged) {
            exercise.plannedTargetReps.last()
        }
        return SetInputUi(
            reps = targetReps.toString(),
            weight = weight?.let(::formatWeight).orEmpty(),
            rpe = null,
            editingSetId = null,
        )
    }

    private companion object {
        const val WEIGHT_STEP = 0.5
    }
}
