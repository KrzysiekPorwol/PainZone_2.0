package com.painzone.ui.session

import com.painzone.domain.exercise.MuscleGroup
import com.painzone.domain.session.Rpe
import java.time.Duration
import java.time.Instant

// S9 — active workout session. M3.5 adds log-a-set UX (input row + logged list).
// Read-only finish (M3.10) arrives later.
sealed interface SessionUiState {
    data object Loading : SessionUiState

    // Session finished or deleted while the screen is open — nothing to show.
    data object NotFound : SessionUiState

    data class Content(
        val planName: String,
        val dayName: String,
        val exercises: List<SessionExerciseUi>,
        val activeIndex: Int,
        // When the session started — backs the elapsed-time line in the finish summary (D2).
        val startedAt: Instant = Instant.EPOCH,
    ) : SessionUiState {
        val activeExercise: SessionExerciseUi get() = exercises[activeIndex]
        val exerciseCount: Int get() = exercises.size
        val position: Int get() = activeIndex + 1
        val hasNext: Boolean get() = activeIndex < exercises.lastIndex
        val hasPrevious: Boolean get() = activeIndex > 0

        // D2 finish dialog summary, computed against [now] (elapsed time is captured on open).
        fun finishSummary(now: Instant): FinishSummary {
            val elapsed = Duration.between(startedAt, now).seconds.coerceAtLeast(0L)
            return FinishSummary(
                totalSets = exercises.sumOf { it.loggedSetCount },
                elapsedSeconds = elapsed,
                tonnage = exercises.sumOf { ex -> ex.loggedSets.sumOf { it.reps * it.weight } },
                unfinishedExercises = exercises.count { it.loggedSetCount < it.plannedSets },
            )
        }
    }
}

// D2 finish summary (S9): totals shown before confirming the session is done.
data class FinishSummary(
    val totalSets: Int,
    val elapsedSeconds: Long,
    val tonnage: Double,
    val unfinishedExercises: Int,
)

data class SessionExerciseUi(
    val snapshotId: Long,
    val exerciseId: Long,
    val name: String,
    val muscleGroup: MuscleGroup,
    val plannedTargetReps: List<Int>,
    val plannedRestSeconds: Int?,
    val loggedSets: List<LoggedSetUi>,
    // Last Set Preview — this exercise's ordered sets from its most recent prior session.
    // Index = series number − 1. Empty = no prior session.
    val lastSession: List<LastSetPreviewUi> = emptyList(),
) {
    val plannedSets: Int get() = plannedTargetReps.size
    val loggedSetCount: Int get() = loggedSets.size

    // Only the freshest set (highest order) is editable inline — "edycja świeżej serii nadpisuje".
    val freshSetId: Long? get() = loggedSets.maxByOrNull { it.order }?.id

    // True when this exercise has been trained in a prior session (drives the empty-state copy).
    val hasPriorSession: Boolean get() = lastSession.isNotEmpty()

    // Series the user is working on now (0-based), clamped to the plan — mirrors the "Seria K/L"
    // header so the preview lines up with the series being logged.
    val currentSetIndex: Int get() = loggedSetCount.coerceAtMost((plannedSets - 1).coerceAtLeast(0))

    // Prior-session set matching the current series, or null when that series wasn't logged then.
    val currentLastSet: LastSetPreviewUi? get() = lastSession.getOrNull(currentSetIndex)
}

data class LoggedSetUi(
    val id: Long,
    val order: Int,
    val reps: Int,
    val weight: Double,
    val rpe: Rpe?,
    // When this set was saved — the rest clock for the next set starts here.
    val completedAt: Instant = Instant.EPOCH,
)

// Rest Timer banner (S9). Count-up since the active exercise's last logged set;
// null = no rest in progress (exercise has no set yet).
data class RestTimerUi(
    val elapsedSeconds: Int,
    val targetSeconds: Int?,
    // The logged set this rest follows — identifies the rest period so the overflow alert
    // (M3.8) fires exactly once per rest, not on every tick past the target.
    val lastSetId: Long = 0L,
) {
    // True once the planned rest is exceeded — drives the over-target accent + overflow alert.
    val isOverTarget: Boolean get() = targetSeconds != null && elapsedSeconds >= targetSeconds
}

// Input row state. editingSetId == null → next save appends; non-null → save overwrites that set.
data class SetInputUi(
    val reps: String = "",
    val weight: String = "",
    val rpe: Rpe? = null,
    val editingSetId: Long? = null,
) {
    val isEditing: Boolean get() = editingSetId != null
    val canSave: Boolean get() = (reps.toIntOrNull() ?: 0) >= 1 && (weight.toDoubleOrNull() ?: 0.0) >= 0.0
}

// Last Set Preview line state (S9). daysAgo = calendar days between the set and today.
data class LastSetPreviewUi(
    val reps: Int,
    val weight: Double,
    val rpe: Rpe?,
    val daysAgo: Int,
)

// Drops a trailing ".0" so whole weights read "60" while half-steps stay "62.5".
fun formatWeight(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()

val Rpe.labelPl: String
    get() = when (this) {
        Rpe.Easy -> "Łatwa"
        Rpe.Normal -> "Normalna"
        Rpe.Hard -> "Ciężka"
    }

// "dziś" / "wczoraj" / "N dni temu" — Polish reads more naturally than a bare "0 dni temu".
fun daysAgoLabel(days: Int): String = when {
    days <= 0 -> "dziś"
    days == 1 -> "wczoraj"
    else -> "$days dni temu"
}

// "reps × kg / RPE — N dni temu"; RPE suffix dropped when not recorded (wireframe S9).
fun lastSetPreviewLine(preview: LastSetPreviewUi): String {
    val rpeSuffix = preview.rpe?.let { " / ${it.labelPl}" }.orEmpty()
    return "${preview.reps} × ${formatWeight(preview.weight)} kg$rpeSuffix — ${daysAgoLabel(preview.daysAgo)}"
}

// Seconds → "m:ss" (rest timer banner reads "1:05", target reads "1:30").
fun formatRestClock(seconds: Int): String {
    val safe = seconds.coerceAtLeast(0)
    return "${safe / 60}:${(safe % 60).toString().padStart(2, '0')}"
}

// Polish count form of "seria" (1 seria · 3 serie · 12 serii) for the finish summary.
fun polishSets(n: Int): String {
    val word = when {
        n == 1 -> "seria"
        n % 100 in 12..14 -> "serii"
        n % 10 in 2..4 -> "serie"
        else -> "serii"
    }
    return "$n $word"
}

// Session length → "1 godz 5 min" / "45 min" / "<1 min" for the finish summary.
fun formatSessionDuration(seconds: Long): String {
    val safe = seconds.coerceAtLeast(0L)
    val hours = safe / 3600
    val minutes = (safe % 3600) / 60
    return when {
        hours > 0 && minutes > 0 -> "$hours godz $minutes min"
        hours > 0 -> "$hours godz"
        minutes > 0 -> "$minutes min"
        else -> "<1 min"
    }
}

// "N serii · czas Y · tonaż Z kg" — the top line of the D2 finish dialog.
fun finishSummaryLine(summary: FinishSummary): String =
    "${polishSets(summary.totalSets)} · czas ${formatSessionDuration(summary.elapsedSeconds)} · " +
        "tonaż ${formatWeight(summary.tonnage)} kg"
