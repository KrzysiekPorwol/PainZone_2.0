package com.painzone.ui.session

import com.painzone.domain.exercise.MuscleGroup
import com.painzone.domain.session.Rpe

// S9 — active workout session. M3.5 adds log-a-set UX (input row + logged list).
// Last Set Preview (M3.6), Rest Timer (M3.7) and read-only finish (M3.10) arrive later.
sealed interface SessionUiState {
    data object Loading : SessionUiState

    // Session finished or deleted while the screen is open — nothing to show.
    data object NotFound : SessionUiState

    data class Content(
        val planName: String,
        val dayName: String,
        val exercises: List<SessionExerciseUi>,
        val activeIndex: Int,
    ) : SessionUiState {
        val activeExercise: SessionExerciseUi get() = exercises[activeIndex]
        val exerciseCount: Int get() = exercises.size
        val position: Int get() = activeIndex + 1
        val hasNext: Boolean get() = activeIndex < exercises.lastIndex
        val hasPrevious: Boolean get() = activeIndex > 0
    }
}

data class SessionExerciseUi(
    val snapshotId: Long,
    val exerciseId: Long,
    val name: String,
    val muscleGroup: MuscleGroup,
    val plannedTargetReps: List<Int>,
    val plannedRestSeconds: Int?,
    val loggedSets: List<LoggedSetUi>,
) {
    val plannedSets: Int get() = plannedTargetReps.size
    val loggedSetCount: Int get() = loggedSets.size

    // Only the freshest set (highest order) is editable inline — "edycja świeżej serii nadpisuje".
    val freshSetId: Long? get() = loggedSets.maxByOrNull { it.order }?.id
}

data class LoggedSetUi(
    val id: Long,
    val order: Int,
    val reps: Int,
    val weight: Double,
    val rpe: Rpe?,
)

// Input row state. editingSetId == null → next save appends; non-null → save overwrites that set.
data class SetInputUi(
    val reps: String = "",
    val weight: String = "",
    val rpe: Rpe? = null,
    val editingSetId: Long? = null,
) {
    val isEditing: Boolean get() = editingSetId != null
    val canSave: Boolean get() = (reps.toIntOrNull() ?: 0) >= 1 && (weight.toDoubleOrNull() ?: 0.0) >= 0.0
}

// Drops a trailing ".0" so whole weights read "60" while half-steps stay "62.5".
fun formatWeight(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
