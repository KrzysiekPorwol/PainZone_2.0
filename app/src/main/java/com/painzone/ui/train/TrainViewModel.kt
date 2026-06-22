package com.painzone.ui.train

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.painzone.domain.plan.PlanRepository
import com.painzone.domain.session.SessionRepository
import com.painzone.domain.session.StartSessionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TrainViewModel @Inject constructor(
    private val planRepository: PlanRepository,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    val uiState: StateFlow<TrainUiState> =
        combine(
            planRepository.observeActive(),
            sessionRepository.observeInProgress(),
        ) { active, inProgress -> active to inProgress }
            .flatMapLatest { (active, inProgress) ->
                if (active == null) {
                    flowOf(TrainUiState.NoActivePlan)
                } else {
                    planRepository.observeDaysByPlan(active.id).map { days ->
                        val firstDay = days.minByOrNull { it.order }
                        TrainUiState.ActivePlan(
                            planName = active.name,
                            startableDay = firstDay?.let { StartableDay(it.id, it.name) },
                            inProgressSessionId = inProgress?.id,
                        )
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = TrainUiState.Loading,
            )

    // One-shot navigation target: the session to open after start/resume.
    private val _openSession = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    val openSession: SharedFlow<Long> = _openSession.asSharedFlow()

    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    fun onStartClick() {
        val state = uiState.value as? TrainUiState.ActivePlan ?: return
        viewModelScope.launch {
            // Resume wins over start: ≤1 in-progress session globally.
            state.inProgressSessionId?.let {
                _openSession.emit(it)
                return@launch
            }
            val day = state.startableDay ?: return@launch
            when (val result = sessionRepository.start(day.dayId)) {
                is StartSessionResult.Success -> _openSession.emit(result.sessionId)
                StartSessionResult.AlreadyInProgress ->
                    sessionRepository.getInProgress()?.let { _openSession.emit(it.id) }
                StartSessionResult.EmptyDay ->
                    _snackbarEvents.emit("Ten dzień nie ma ćwiczeń.")
                StartSessionResult.DayNotFound ->
                    _snackbarEvents.emit("Nie znaleziono dnia treningowego.")
            }
        }
    }
}
