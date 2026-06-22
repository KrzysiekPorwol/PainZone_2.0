package com.painzone.ui.train

// S1 — Trenuj. M3.4 scope: resume banner (in-progress session) + SmartCard (active plan).
// The two are independent: an in-progress session keeps its own plan/day snapshot even
// after the active plan changes, so "Wznów" always returns to the right session.
sealed interface TrainUiState {
    data object Loading : TrainUiState
    data class Loaded(
        // In-progress session to resume; shows the session's own snapshot, not the active plan.
        val resume: ResumeInfo?,
        // Active plan SmartCard; null when no plan is active.
        val activePlan: ActivePlanInfo?,
    ) : TrainUiState {
        val isEmpty: Boolean get() = resume == null && activePlan == null
    }
}

data class ResumeInfo(
    val sessionId: Long,
    val planName: String,
    val dayName: String,
)

data class ActivePlanInfo(
    val planName: String,
    // First day of the plan (MIN order); null when the plan has no days yet.
    val startableDay: StartableDay?,
)

data class StartableDay(val dayId: Long, val dayName: String)
