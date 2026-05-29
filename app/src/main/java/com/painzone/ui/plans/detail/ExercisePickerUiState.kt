package com.painzone.ui.plans.detail

// S6 — exercise picker. Empty = no active exercises in the library at all;
// noResults = library has exercises but the current search filters them all out.
sealed interface ExercisePickerUiState {
    data object Loading : ExercisePickerUiState
    data object Empty : ExercisePickerUiState
    data class Content(
        val sections: List<PickerSection>,
        val noResults: Boolean,
    ) : ExercisePickerUiState
}

data class PickerSection(
    val groupLabel: String,
    val items: List<PickerItem>,
)

data class PickerItem(
    val id: Long,
    val name: String,
)