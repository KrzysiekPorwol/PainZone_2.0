package com.painzone.ui.session

import com.painzone.domain.exercise.MuscleGroup
import org.junit.Assert.assertEquals
import java.time.Instant
import org.junit.Test

class FinishSummaryFormatTest {

    @Test
    fun `polishSets uses the right count form`() {
        assertEquals("1 seria", polishSets(1))
        assertEquals("2 serie", polishSets(2))
        assertEquals("4 serie", polishSets(4))
        assertEquals("5 serii", polishSets(5))
        assertEquals("12 serii", polishSets(12)) // 12-14 take "serii" despite ending in 2-4
        assertEquals("22 serie", polishSets(22))
        assertEquals("0 serii", polishSets(0))
    }

    @Test
    fun `formatSessionDuration reads naturally`() {
        assertEquals("<1 min", formatSessionDuration(0))
        assertEquals("<1 min", formatSessionDuration(-5)) // clock skew clamps
        assertEquals("45 min", formatSessionDuration(45 * 60))
        assertEquals("1 godz", formatSessionDuration(3600))
        assertEquals("1 godz 5 min", formatSessionDuration(3600 + 5 * 60))
    }

    @Test
    fun `finishSummaryLine combines sets, time and tonnage`() {
        val summary = FinishSummary(
            totalSets = 6,
            elapsedSeconds = 3600 + 5 * 60,
            tonnage = 1230.0,
            unfinishedExercises = 1,
        )
        assertEquals("6 serii · czas 1 godz 5 min · tonaż 1230 kg", finishSummaryLine(summary))
    }

    @Test
    fun `finishSummary totals sets, tonnage and unfinished exercises`() {
        val exercises = listOf(
            SessionExerciseUi(
                snapshotId = 1L,
                exerciseId = 10L,
                name = "Bench",
                muscleGroup = MuscleGroup.Chest,
                plannedTargetReps = listOf(10, 10),
                plannedRestSeconds = 90,
                loggedSets = listOf(
                    LoggedSetUi(1L, 1, 10, 60.0, null),
                    LoggedSetUi(2L, 2, 8, 62.5, null),
                ),
            ),
            // One set of two planned → unfinished.
            SessionExerciseUi(
                snapshotId = 2L,
                exerciseId = 11L,
                name = "Press",
                muscleGroup = MuscleGroup.Shoulders,
                plannedTargetReps = listOf(10, 10),
                plannedRestSeconds = 60,
                loggedSets = listOf(LoggedSetUi(3L, 1, 10, 40.0, null)),
            ),
        )
        val started = Instant.ofEpochSecond(1000)
        val content = SessionUiState.Content("PPL", "Push", exercises, 0, startedAt = started)

        val summary = content.finishSummary(now = started.plusSeconds(1800))

        assertEquals(3, summary.totalSets)
        assertEquals(1800L, summary.elapsedSeconds)
        // 10*60 + 8*62.5 + 10*40 = 600 + 500 + 400 = 1500
        assertEquals(1500.0, summary.tonnage, 0.0)
        assertEquals(1, summary.unfinishedExercises)
    }
}
