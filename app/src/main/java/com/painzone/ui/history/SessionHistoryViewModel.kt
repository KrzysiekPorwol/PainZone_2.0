package com.painzone.ui.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.painzone.domain.session.SessionRepository
import com.painzone.ui.navigation.SessionHistory
import dagger.hilt.android.lifecycle.HiltViewModel
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
class SessionHistoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    sessionRepository: SessionRepository,
) : ViewModel() {

    // Initial filter from the route: null = "Chronologicznie" (all), a name = "Po planie" (S12).
    private val selectedPlan = MutableStateFlow(savedStateHandle.toRoute<SessionHistory>().planNameFilter)
    val selectedFilter: StateFlow<String?> = selectedPlan.asStateFlow()

    // Dropdown options: "Wszystkie" + every plan that has at least one finished session.
    val filterOptions: StateFlow<List<PlanFilterOption>> = sessionRepository.observeSessionPlanNames()
        .map { names -> listOf(ALL_PLANS_FILTER) + names.map { PlanFilterOption(it, it) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = listOf(ALL_PLANS_FILTER),
        )

    val uiState: StateFlow<SessionHistoryUiState> = selectedPlan
        .flatMapLatest { filter -> sessionRepository.observeCompleted(filter) }
        .map { sessions ->
            if (sessions.isEmpty()) SessionHistoryUiState.Empty
            else SessionHistoryUiState.Content(sessions.toCardUi())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SessionHistoryUiState.Loading,
        )

    fun selectFilter(planName: String?) {
        selectedPlan.value = planName
    }
}
