package com.painzone.domain.plan

// Read-model for the plans list (S2): plan identity + derived day count.
// Projection, not an entity — intentionally without TrainingPlan's invariants.
data class PlanSummary(
    val id: Long,
    val name: String,
    val isActive: Boolean,
    val dayCount: Int,
    val icon: PlanIcon = PlanIcon.DEFAULT,
)