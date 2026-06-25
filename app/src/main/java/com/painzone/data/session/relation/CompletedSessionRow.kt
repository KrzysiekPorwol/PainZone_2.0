package com.painzone.data.session.relation

import androidx.room.ColumnInfo
import java.time.Instant

// Flat projection for the S13 history list: one finished session joined with its
// aggregated set count and tonnage (SUM(reps × weight)). Sessions with no logged
// sets still appear (set_count = 0, tonnage = 0) via the LEFT JOIN.
data class CompletedSessionRow(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "plan_name_snapshot") val planNameSnapshot: String,
    @ColumnInfo(name = "day_name_snapshot") val dayNameSnapshot: String,
    @ColumnInfo(name = "started_at") val startedAt: Instant,
    @ColumnInfo(name = "set_count") val setCount: Int,
    @ColumnInfo(name = "tonnage") val tonnage: Double,
)
