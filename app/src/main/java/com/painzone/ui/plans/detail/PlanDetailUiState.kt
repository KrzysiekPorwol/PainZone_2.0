package com.painzone.ui.plans.detail

import com.painzone.domain.plan.PlannedDay

// S4 — plan detail. NotFound covers a plan deleted while its screen is open.
sealed interface PlanDetailUiState {
    data object Loading : PlanDetailUiState
    data object NotFound : PlanDetailUiState
    data class Content(
        val planName: String,
        val isActive: Boolean,
        val days: List<PlannedDay>,
    ) : PlanDetailUiState
}