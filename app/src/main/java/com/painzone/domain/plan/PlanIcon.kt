package com.painzone.domain.plan

// Pickable icon for a plan, chosen at creation and shown on the plans list (S2)
// and the active-plan card (S1). Stored by enum name; the UI layer maps each
// value to a Material icon (domain stays free of Compose deps).
enum class PlanIcon {
    FITNESS_CENTER,
    DIRECTIONS_RUN,
    SELF_IMPROVEMENT,
    SPORTS_GYMNASTICS,
    SPORTS_MARTIAL_ARTS,
    DIRECTIONS_BIKE,
    POOL,
    HIKING,
    MONITOR_HEART,
    BOLT,
    FAVORITE,
    SPORTS_SCORE,
    ;

    companion object {
        val DEFAULT = FITNESS_CENTER

        // Tolerant parse: unknown/legacy names fall back to the default icon
        // so a renamed enum value can never crash reads.
        fun fromName(name: String?): PlanIcon =
            entries.firstOrNull { it.name == name } ?: DEFAULT
    }
}
