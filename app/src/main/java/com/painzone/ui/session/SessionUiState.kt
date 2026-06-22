package com.painzone.ui.session

import com.painzone.domain.exercise.MuscleGroup

// S9 — active workout session. M3.4 scope: exercise list from plan + active exercise + nav.
// Log input (M3.5), Last Set Preview (M3.6), Rest Timer (M3.7) and finish (M3.10) arrive later.
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
    val name: String,
    val muscleGroup: MuscleGroup,
    val plannedTargetReps: List<Int>,
    val plannedRestSeconds: Int?,
    val loggedSetCount: Int,
) {
    val plannedSets: Int get() = plannedTargetReps.size
}
