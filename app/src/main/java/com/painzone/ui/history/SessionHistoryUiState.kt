package com.painzone.ui.history

import com.painzone.domain.session.CompletedSession
import com.painzone.ui.session.formatWeight
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

sealed interface SessionHistoryUiState {
    data object Loading : SessionHistoryUiState

    // No finished sessions match the active filter — the user changes it via the dropdown.
    data object Empty : SessionHistoryUiState

    data class Content(val sessions: List<SessionCardUi>) : SessionHistoryUiState
}

// One S13 history card. Tap → S14 (read-only detail, wired in M5.3).
data class SessionCardUi(
    val sessionId: Long,
    val title: String, // "DD.MM · Plan · Dzień"
    val stats: String, // "N serii · tonaż Z kg"
)

// The plan dropdown filter. `name` null = "Wszystkie" (no plan filter).
data class PlanFilterOption(
    val name: String?,
    val label: String,
)

val ALL_PLANS_FILTER = PlanFilterOption(name = null, label = "Wszystkie")

private val cardDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM")

fun List<CompletedSession>.toCardUi(zone: ZoneId = ZoneId.systemDefault()): List<SessionCardUi> =
    map { session ->
        SessionCardUi(
            sessionId = session.sessionId,
            title = cardTitle(session.startedAt, session.planNameSnapshot, session.dayNameSnapshot, zone),
            stats = "${setCountLabel(session.setCount)} · tonaż ${formatWeight(session.tonnage)} kg",
        )
    }

private fun cardTitle(startedAt: Instant, planName: String, dayName: String, zone: ZoneId): String {
    val date = startedAt.atZone(zone).format(cardDateFormatter)
    return "$date · $planName · $dayName"
}

// Polish declension for "N series": 1 → "seria", 2-4 → "serie", 5+ → "serii".
private fun setCountLabel(count: Int): String {
    val mod10 = count % 10
    val mod100 = count % 100
    val noun = when {
        count == 1 -> "seria"
        mod10 in 2..4 && mod100 !in 12..14 -> "serie"
        else -> "serii"
    }
    return "$count $noun"
}
