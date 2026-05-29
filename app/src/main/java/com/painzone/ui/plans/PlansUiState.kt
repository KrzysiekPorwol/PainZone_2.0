package com.painzone.ui.plans

import com.painzone.domain.plan.PlanSummary

sealed interface PlansUiState {
    data object Loading : PlansUiState
    data object Empty : PlansUiState
    data class Content(val items: List<PlanSummary>) : PlansUiState
}