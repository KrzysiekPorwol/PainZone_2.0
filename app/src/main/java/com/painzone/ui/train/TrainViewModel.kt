package com.painzone.ui.train

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.painzone.domain.plan.PlanRepository
import com.painzone.domain.session.SessionRepository
import com.painzone.domain.session.StartSessionResult
import com.painzone.domain.session.suggestNextDay
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
                val resume = inProgress?.let {
                    ResumeInfo(
                        sessionId = it.id,
                        planName = it.planNameSnapshot,
                        dayName = it.dayNameSnapshot,
                    )
                }
                if (active == null) {
                    flowOf(TrainUiState.Loaded(resume = resume, activePlan = null))
                } else {
                    planRepository.observeDaysByPlan(active.id).map { days ->
                        val ordered = days.sortedBy { it.order }
                        // Rotation anchor = last trained day among this plan's days; recomputed on
                        // each emission (e.g. after a session finishes the suggestion advances).
                        val anchor = sessionRepository.lastStartedDayId(ordered.map { it.id })
                        val suggested = suggestNextDay(ordered, anchor)
                        TrainUiState.Loaded(
                            resume = resume,
                            activePlan = ActivePlanInfo(
                                planName = active.name,
                                suggestedDay = suggested?.let { StartableDay(it.id, it.name) },
                                allDays = ordered.map { StartableDay(it.id, it.name) },
                            ),
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

    // Starts a specific day — the suggested one (SmartCard "Zacznij") or any other the user picks.
    fun onStartDay(dayId: Long) {
        viewModelScope.launch {
            when (val result = sessionRepository.start(dayId)) {
                is StartSessionResult.Success -> _openSession.emit(result.sessionId)
                // ≤1 in-progress globally: fall back to resuming the existing one.
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
