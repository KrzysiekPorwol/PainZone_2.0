package com.painzone.ui.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.painzone.domain.exercise.ExerciseRepository
import com.painzone.domain.session.SessionRepository
import com.painzone.ui.navigation.SessionDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SessionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    sessionRepository: SessionRepository,
    exerciseRepository: ExerciseRepository,
) : ViewModel() {

    private val sessionId: Long = savedStateHandle.toRoute<SessionDetail>().sessionId

    // Snapshot graph (read-only) joined with the live set of soft-deleted exercise ids so the
    // "usunięte" marker reflects the moment of viewing, not the moment of the session.
    val uiState: StateFlow<SessionDetailUiState> = combine(
        sessionRepository.observeSessionDetail(sessionId),
        exerciseRepository.observeDeletedIds(),
    ) { detail, deletedIds ->
        if (detail == null) SessionDetailUiState.NotFound
        else detail.toContent(deletedIds)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SessionDetailUiState.Loading,
    )
}
