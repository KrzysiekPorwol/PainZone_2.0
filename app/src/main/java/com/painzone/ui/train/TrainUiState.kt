package com.painzone.ui.train

// S1 — Trenuj. M2.8 scope: SmartCard placeholder reflecting the active plan only.
// PlanList, day suggestion and "Zacznij" wiring arrive with sessions (M3).
sealed interface TrainUiState {
    data object Loading : TrainUiState
    data object NoActivePlan : TrainUiState
    data class ActivePlan(val planName: String) : TrainUiState
}