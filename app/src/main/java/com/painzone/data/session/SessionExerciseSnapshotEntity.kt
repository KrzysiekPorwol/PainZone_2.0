package com.painzone.data.session

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.painzone.data.exercise.ExerciseEntity
import com.painzone.domain.exercise.MuscleGroup

@Entity(
    tableName = "session_exercise_snapshot",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["session_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            // Exercise may be soft-deleted; reference kept for navigation/aggregation.
            onDelete = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [Index("session_id"), Index("exercise_id")],
)
data class SessionExerciseSnapshotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "session_id") val sessionId: Long,
    @ColumnInfo(name = "exercise_id") val exerciseId: Long,
    @ColumnInfo(name = "exercise_name_snapshot") val exerciseNameSnapshot: String,
    @ColumnInfo(name = "muscle_group_snapshot") val muscleGroupSnapshot: MuscleGroup,
    @ColumnInfo(name = "order_in_session") val order: Int,
    @ColumnInfo(name = "planned_target_reps") val plannedTargetReps: List<Int>,
    @ColumnInfo(name = "planned_rest_seconds") val plannedRestSeconds: Int?,
)
