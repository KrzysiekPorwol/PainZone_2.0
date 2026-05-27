package com.painzone.domain.session

// Explicit intValue (not ordinal) — resilient to reorder of enum entries.
enum class Rpe(val intValue: Int) {
    Easy(1),
    Normal(2),
    Hard(3),
    ;

    companion object {
        fun fromIntValue(value: Int): Rpe = entries.first { it.intValue == value }
    }
}