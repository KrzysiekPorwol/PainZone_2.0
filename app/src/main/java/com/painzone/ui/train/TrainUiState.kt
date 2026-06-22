package com.painzone.ui.train

// S1 — Trenuj. M3.4 scope: SmartCard starts/resumes a session on the active plan.
// Full smart day suggestion (rotate by last session) + PlanList expand are deferred.
sealed interface TrainUiState {
    data object Loading : TrainUiState
    data object NoActivePlan : TrainUiState
    data class ActivePlan(
        val planName: String,
        // First day of the plan (MIN order); null when the plan has no days yet.
        val startableDay: StartableDay?,
        // Non-null when a session is already in progress — SmartCard offers "Wznów".
        val inProgressSessionId: Long?,
    ) : TrainUiState
}

data class StartableDay(val dayId: Long, val dayName: String)
