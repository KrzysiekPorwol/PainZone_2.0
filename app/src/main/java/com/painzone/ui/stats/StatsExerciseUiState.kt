package com.painzone.ui.stats

import com.painzone.domain.session.Rpe
import com.painzone.domain.stats.StatsSet
import com.painzone.ui.session.formatWeight
import com.painzone.ui.session.labelPl
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

sealed interface StatsUiState {
    data object Loading : StatsUiState

    // No logged sets for this exercise in the selected period — the user changes the filter.
    data object Empty : StatsUiState

    data class Content(val sessions: List<StatsSessionUi>) : StatsUiState
}

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
