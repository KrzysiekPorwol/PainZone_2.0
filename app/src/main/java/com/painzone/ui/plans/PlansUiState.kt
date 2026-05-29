package com.painzone.ui.plans

import com.painzone.domain.plan.PlanSummary

sealed interface PlansUiState {
    data object Loading : PlansUiState
    data object Empty : PlansUiState
    data class Content(val items: List<PlanSummary>) : PlansUiState
}

sealed interface DeletePlanDialogState {
    data object Hidden : DeletePlanDialogState
    data class Visible(val planId: Long, val planName: String) : DeletePlanDialogState
}