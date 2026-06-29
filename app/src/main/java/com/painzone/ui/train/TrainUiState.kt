package com.painzone.ui.train

import com.painzone.domain.plan.PlanIcon

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
    val planIcon: PlanIcon = PlanIcon.DEFAULT,
    // Smart suggestion: the day to train next (rotation after the last trained day). Null when
    // the plan has no days yet. Always one of [allDays].
    val suggestedDay: StartableDay?,
    // Every day of the plan in order — each is startable, so the user can skip the rotation
    // (e.g. jump straight to Legs after missing Push).
    val allDays: List<StartableDay>,
)

data class StartableDay(val dayId: Long, val dayName: String)
