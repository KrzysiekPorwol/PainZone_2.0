package com.painzone.ui.train

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
class TrainViewModel @Inject constructor(
    repository: PlanRepository,
) : ViewModel() {

    val uiState: StateFlow<TrainUiState> = repository.observeActive()
        .map { active ->
            if (active == null) TrainUiState.NoActivePlan
            else TrainUiState.ActivePlan(active.name)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TrainUiState.Loading,
        )
}