package com.painzone.data.session

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.painzone.domain.session.Rpe
import java.time.Instant

@Entity(
    tableName = "logged_set",
    foreignKeys = [
        ForeignKey(
            entity = SessionExerciseSnapshotEntity::class,
            parentColumns = ["id"],
            childColumns = ["session_exercise_snapshot_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("session_exercise_snapshot_id")],
)
data class LoggedSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "session_exercise_snapshot_id") val sessionExerciseSnapshotId: Long,
    @ColumnInfo(name = "order_in_exercise") val order: Int,
    @ColumnInfo(name = "reps") val reps: Int,
    @ColumnInfo(name = "weight") val weight: Double,
    @ColumnInfo(name = "rpe") val rpe: Rpe?,
    @ColumnInfo(name = "completed_at") val completedAt: Instant,
)
