package com.painzone.ui.plans

// In-memory form buffer for PlanCreateScreen — nothing is persisted until Save (✓).
// `days` holds session names in display order; order is derived from list index on save.
// Session count is driven by a 1..7 stepper; names start blank and the user fills them in.
data class PlanCreateUiState(
    val name: String = "",
    val days: List<String> = defaultDays(DEFAULT_DAY_COUNT),
    val nameError: String? = null,
    val saving: Boolean = false,
) {
    // Trimmed session names; used by validation so trailing spaces don't fake uniqueness.
    private val trimmedDays: List<String> get() = days.map { it.trim() }

    val hasBlankDay: Boolean get() = trimmedDays.any { it.isEmpty() }

    val hasDuplicateDay: Boolean
        get() = trimmedDays.map { it.lowercase() }.let { it.size != it.toSet().size }

    val canSave: Boolean
        get() = name.trim().isNotEmpty() && !hasBlankDay && !hasDuplicateDay && !saving

    // Pristine = default name (blank) and default session set → no discard prompt on back.
    val isDirty: Boolean
        get() = name.trim().isNotEmpty() || days != defaultDays(DEFAULT_DAY_COUNT)

    companion object {
        const val MIN_DAY_COUNT = 1
        const val MAX_DAY_COUNT = 7
        const val DEFAULT_DAY_COUNT = 3

        fun defaultDays(count: Int): List<String> = List(count) { "" }
    }
}