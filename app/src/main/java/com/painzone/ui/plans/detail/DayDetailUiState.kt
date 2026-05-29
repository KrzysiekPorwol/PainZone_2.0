package com.painzone.ui.plans.detail

// S5 — exercises in a day. A row pairs the PlannedExercise params with the
// resolved Exercise name; isDeleted marks a soft-deleted Exercise (FK is NO ACTION,
// so the reference survives) — shown with a marker, params still editable.
sealed interface DayDetailUiState {
    data object Loading : DayDetailUiState
    data class Content(val rows: List<PlannedExerciseRow>) : DayDetailUiState
}

data class PlannedExerciseRow(
    val plannedExerciseId: Long,
    val exerciseId: Long,
    val name: String,
    val isDeleted: Boolean,
    val targetReps: List<Int>,
    val restSeconds: Int?,
)