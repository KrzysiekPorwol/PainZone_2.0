package com.painzone.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.painzone.domain.session.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SessionPlanPickerViewModel @Inject constructor(
    sessionRepository: SessionRepository,
) : ViewModel() {

    // S12: plans (by name snapshot) that have at least one finished session → S13 filtered.
    val uiState: StateFlow<SessionPlanPickerUiState> = sessionRepository.observeSessionPlanNames()
        .map { names ->
            if (names.isEmpty()) SessionPlanPickerUiState.Empty
            else SessionPlanPickerUiState.Content(names)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SessionPlanPickerUiState.Loading,
        )
}

sealed interface SessionPlanPickerUiState {
    data object Loading : SessionPlanPickerUiState
    data object Empty : SessionPlanPickerUiState
    data class Content(val planNames: List<String>) : SessionPlanPickerUiState
}
