package com.painzone.ui.plans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.painzone.domain.plan.PlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class PlansViewModel @Inject constructor(
    private val repository: PlanRepository,
) : ViewModel() {

    val uiState: StateFlow<PlansUiState> = repository.observeSummaries()
        .map { summaries ->
            if (summaries.isEmpty()) PlansUiState.Empty
            else PlansUiState.Content(summaries)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlansUiState.Loading,
        )
}