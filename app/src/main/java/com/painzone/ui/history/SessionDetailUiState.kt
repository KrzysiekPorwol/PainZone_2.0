package com.painzone.ui.history

import com.painzone.domain.session.LoggedSet
import com.painzone.domain.session.SessionDetail
import com.painzone.ui.session.formatSessionDuration
import com.painzone.ui.session.formatWeight
import com.painzone.ui.session.labelPl
import java.time.Duration
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// S14 — read-only detail of a finished session. Rendered entirely from the session snapshot
// (plan/day/exercise names frozen at the moment of the session), so it survives later renames
// and deletes. The only live bit is the per-exercise "usunięte" marker (M4.5 semantics).
sealed interface SessionDetailUiState {
    data object Loading : SessionDetailUiState

    // Session no longer exists (e.g. its plan day was deleted and the row went with it).
    data object NotFound : SessionDetailUiState

    data class Content(
        val title: String, // "DD.MM · Plan · Dzień"
        val stats: String, // "czas Y · tonaż Z kg"
        val exercises: List<SessionExerciseDetailUi>,
    ) : SessionDetailUiState
}

data class SessionExerciseDetailUi(
    val snapshotId: Long,
    val name: String,
    // Exercise soft-deleted at the moment of viewing → "usunięte" marker. Sets still render.
    val isDeleted: Boolean,
    val sets: List<String>, // each "reps × kg · RPE · po Xs odpocz."
)

private val headerDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM")

fun SessionDetail.toContent(
    deletedExerciseIds: Set<Long>,
    zone: ZoneId = ZoneId.systemDefault(),
): SessionDetailUiState.Content {
    val date = session.startedAt.atZone(zone).format(headerDateFormatter)
    // A finished session always has finishedAt; fall back to 0 defensively (NotFound guards the
    // in-progress case upstream — only finished sessions reach S14).
    val durationSeconds = session.finishedAt
        ?.let { Duration.between(session.startedAt, it).seconds }
        ?: 0L
    val tonnage = exercises.sumOf { ex -> ex.loggedSets.sumOf { it.reps * it.weight } }
    return SessionDetailUiState.Content(
        title = "$date · ${session.planNameSnapshot} · ${session.dayNameSnapshot}",
        stats = "czas ${formatSessionDuration(durationSeconds)} · tonaż ${formatWeight(tonnage)} kg",
        exercises = exercises.map { ex ->
            SessionExerciseDetailUi(
                snapshotId = ex.snapshot.id,
                name = ex.snapshot.exerciseNameSnapshot,
                isDeleted = ex.snapshot.exerciseId in deletedExerciseIds,
                sets = ex.loggedSets.map { it.toLine() },
            )
        },
    )
}

private fun LoggedSet.toLine(): String {
    val parts = buildList {
        add("$reps × ${formatWeight(weight)} kg")
        rpe?.let { add("RPE ${it.labelPl}") }
        add(restLine(order, restBeforeSeconds))
    }
    return parts.joinToString(" · ")
}

// First series opens the exercise — no rest precedes it (same rule as Stats S10).
private fun restLine(order: Int, restBeforeSeconds: Int?): String =
    if (order <= 1 || restBeforeSeconds == null) "—" else "po ${restBeforeSeconds}s odpocz."
