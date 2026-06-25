package com.painzone.ui.stats

import com.painzone.domain.session.Rpe
import com.painzone.domain.stats.StatsSet
import com.painzone.domain.stats.estimatedOneRepMax
import com.painzone.ui.session.daysAgoLabel
import com.painzone.ui.session.formatWeight
import com.painzone.ui.session.labelPl
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

sealed interface StatsUiState {
    data object Loading : StatsUiState

    // No logged sets for this exercise in the selected period — the user changes the filter.
    data object Empty : StatsUiState

    // `best` is the MAX(1RM est.) set within the active filter (US-6 "czy idę w górę" in ≤10s).
    data class Content(val best: BestSetUi, val sessions: List<StatsSessionUi>) : StatsUiState
}

// Top card on S10 — the highest estimated 1RM set in the current filter window.
data class BestSetUi(
    val text: String, // "Best: reps × kg · 1RM≈Y kg · N dni temu"
)

// One finished session's logged sets for this exercise, as one S10 list section.
data class StatsSessionUi(
    val sessionId: Long,
    val header: String, // "DD.MM · Plan · Dzień"
    val sets: List<StatsSetUi>,
)

data class StatsSetUi(
    val setId: Long,
    val text: String, // "reps × kg · RPE · po Xs odpocz."
)

private val sectionDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM")

// Flat, newest-session-first set list → sections grouped by session, preserving order.
fun List<StatsSet>.toSessionUi(zone: ZoneId = ZoneId.systemDefault()): List<StatsSessionUi> =
    groupBy { it.sessionId }
        .map { (sessionId, sets) ->
            val first = sets.first()
            StatsSessionUi(
                sessionId = sessionId,
                header = sessionHeader(first.sessionStartedAt, first.planNameSnapshot, first.dayNameSnapshot, zone),
                sets = sets.map { it.toSetUi() },
            )
        }

// Best set = MAX estimated 1RM in the window. On a tie the newest set wins (list is
// newest-session-first, and maxByOrNull keeps the first maximal element). The "N dni temu"
// anchor is the session date shown in the matching section header (sessionStartedAt).
// Contract: only called on a non-empty list (the VM routes empty → Empty state).
fun List<StatsSet>.toBestSetUi(today: LocalDate, zone: ZoneId = ZoneId.systemDefault()): BestSetUi {
    val best = maxBy { it.estimatedOneRepMax() }
    val day = best.sessionStartedAt.atZone(zone).toLocalDate()
    val daysAgo = ChronoUnit.DAYS.between(day, today).toInt().coerceAtLeast(0)
    val oneRm = Math.round(best.estimatedOneRepMax()).toInt()
    val text = "Best: ${best.reps} × ${formatWeight(best.weight)} kg · 1RM≈$oneRm kg · ${daysAgoLabel(daysAgo)}"
    return BestSetUi(text)
}

private fun sessionHeader(startedAt: Instant, planName: String, dayName: String, zone: ZoneId): String {
    val date = startedAt.atZone(zone).format(sectionDateFormatter)
    return "$date · $planName · $dayName"
}

private fun StatsSet.toSetUi(): StatsSetUi {
    val parts = buildList {
        add("$reps × ${formatWeight(weight)} kg")
        rpe?.let { add(it.statsLabel()) }
        add(restLabel(order, restBeforeSeconds))
    }
    return StatsSetUi(setId = setId, text = parts.joinToString(" · "))
}

// First series has no "rest before" — it opened the exercise. Same for a missing value.
private fun restLabel(order: Int, restBeforeSeconds: Int?): String =
    if (order <= 1 || restBeforeSeconds == null) "—" else "po ${restBeforeSeconds}s odpocz."

private fun Rpe.statsLabel(): String = "RPE ${labelPl}"
