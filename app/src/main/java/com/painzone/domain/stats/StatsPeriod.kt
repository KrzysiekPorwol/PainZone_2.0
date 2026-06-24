package com.painzone.domain.stats

import java.time.Instant
import java.time.temporal.ChronoUnit

// Time window for Stats Lite (US-6 · S10). ALL = no lower bound.
// Year is approximated as 365 days — good enough for a progress glance, no calendar math.
enum class StatsPeriod(val days: Long?) {
    LAST_30_DAYS(30),
    LAST_90_DAYS(90),
    LAST_YEAR(365),
    ALL(null);

    // Lower bound (inclusive) for the window, or null for ALL. Pure of "now" so it's testable.
    fun since(now: Instant): Instant? = days?.let { now.minus(it, ChronoUnit.DAYS) }

    companion object {
        // S10 opens on 90d (PRD US-6).
        val DEFAULT = LAST_90_DAYS
    }
}
