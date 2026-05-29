package com.painzone.ui.plans.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.painzone.domain.plan.PlanRepository
import com.painzone.ui.navigation.PlanDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class PlanDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: PlanRepository,
) : ViewModel() {

    private val planId: Long = savedStateHandle.toRoute<PlanDetail>().planId

    val uiState: StateFlow<PlanDetailUiState> =
        repository.observePlanWithDays(planId)
            .map { planWithDays ->
                if (planWithDays == null) {
                    PlanDetailUiState.NotFound
                } else {
                    PlanDetailUiState.Content(
                        planName = planWithDays.plan.name,
                        days = planWithDays.days.map { it.day },
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PlanDetailUiState.Loading,
            )
}