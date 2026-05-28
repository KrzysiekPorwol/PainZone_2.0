package com.painzone.data.plan

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.painzone.data.exercise.ExerciseEntity

@Entity(
    tableName = "planned_exercise",
    foreignKeys = [
        ForeignKey(
            entity = PlannedDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["planned_day_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [Index("planned_day_id"), Index("exercise_id")],
)
data class PlannedExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "planned_day_id") val plannedDayId: Long,
    @ColumnInfo(name = "exercise_id") val exerciseId: Long,
    @ColumnInfo(name = "order_in_day") val order: Int,
    @ColumnInfo(name = "target_reps") val targetReps: List<Int>,
    @ColumnInfo(name = "rest_seconds") val restSeconds: Int?,
)