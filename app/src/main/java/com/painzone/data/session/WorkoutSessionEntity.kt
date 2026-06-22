package com.painzone.data.session

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.painzone.data.plan.PlannedDayEntity
import java.time.Instant

@Entity(
    tableName = "workout_session",
    foreignKeys = [
        ForeignKey(
            entity = PlannedDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["planned_day_id"],
            // Plan can be hard-deleted; session survives via name snapshots.
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("planned_day_id")],
)
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "planned_day_id") val plannedDayId: Long?,
    @ColumnInfo(name = "plan_name_snapshot") val planNameSnapshot: String,
    @ColumnInfo(name = "day_name_snapshot") val dayNameSnapshot: String,
    @ColumnInfo(name = "started_at") val startedAt: Instant,
    @ColumnInfo(name = "finished_at") val finishedAt: Instant?,
)
