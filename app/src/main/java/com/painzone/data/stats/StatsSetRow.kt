package com.painzone.data.stats

import androidx.room.ColumnInfo
import com.painzone.domain.session.Rpe
import java.time.Instant

// Flat projection of StatsDao.observeSets — logged_set columns joined with the owning
// session's snapshot context. Column names alias the SELECT in StatsDao.
data class StatsSetRow(
    @ColumnInfo(name = "set_id") val setId: Long,
    @ColumnInfo(name = "session_id") val sessionId: Long,
    @ColumnInfo(name = "session_started_at") val sessionStartedAt: Instant,
    @ColumnInfo(name = "plan_name_snapshot") val planNameSnapshot: String,
    @ColumnInfo(name = "day_name_snapshot") val dayNameSnapshot: String,
    @ColumnInfo(name = "order_in_exercise") val order: Int,
    @ColumnInfo(name = "reps") val reps: Int,
    @ColumnInfo(name = "weight") val weight: Double,
    @ColumnInfo(name = "rpe") val rpe: Rpe?,
    @ColumnInfo(name = "rest_before_seconds") val restBeforeSeconds: Int?,
    @ColumnInfo(name = "completed_at") val completedAt: Instant,
)
