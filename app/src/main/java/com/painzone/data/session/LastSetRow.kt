package com.painzone.data.session

import androidx.room.ColumnInfo
import com.painzone.domain.session.Rpe
import java.time.Instant

// Projection for LoggedSetDao.lastSetForExercise — just the fields the preview line needs.
data class LastSetRow(
    @ColumnInfo(name = "reps") val reps: Int,
    @ColumnInfo(name = "weight") val weight: Double,
    @ColumnInfo(name = "rpe") val rpe: Rpe?,
    @ColumnInfo(name = "completed_at") val completedAt: Instant,
)
