package com.painzone.ui.progress

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
class ProgressHubViewModel @Inject constructor(
    sessionRepository: SessionRepository,
) : ViewModel() {

    // The hub's three history modes are all empty until at least one session is finished,
    // so a single "has any completed session" flag drives the shared empty state (S3).
    val uiState: StateFlow<ProgressHubUiState> = sessionRepository.observeHasCompletedSessions()
        .map { hasHistory ->
            if (hasHistory) ProgressHubUiState.Ready else ProgressHubUiState.Empty
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProgressHubUiState.Loading,
        )
}

sealed interface ProgressHubUiState {
    data object Loading : ProgressHubUiState
    data object Empty : ProgressHubUiState
    data object Ready : ProgressHubUiState
}
