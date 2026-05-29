package com.painzone.ui.plans

// In-memory form buffer for PlanCreateScreen — nothing is persisted until Save (✓).
// `days` holds day names in display order; order is derived from list index on save.
data class PlanCreateUiState(
    val name: String = "",
    val days: List<String> = emptyList(),
    val nameError: String? = null,
    val saving: Boolean = false,
) {
    val canSave: Boolean get() = name.trim().isNotEmpty() && !saving
    val isDirty: Boolean get() = name.trim().isNotEmpty() || days.isNotEmpty()
}