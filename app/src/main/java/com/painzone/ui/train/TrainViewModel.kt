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
                        val firstDay = days.minByOrNull { it.order }
                        TrainUiState.Loaded(
                            resume = resume,
                            activePlan = ActivePlanInfo(
                                planName = active.name,
                                startableDay = firstDay?.let { StartableDay(it.id, it.name) },
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

    fun onStartClick() {
        val loaded = uiState.value as? TrainUiState.Loaded ?: return
        val day = loaded.activePlan?.startableDay ?: return
        viewModelScope.launch {
            when (val result = sessionRepository.start(day.dayId)) {
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
