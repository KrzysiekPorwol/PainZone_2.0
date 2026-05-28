package com.painzone.data.plan

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "planned_day",
    foreignKeys = [
        ForeignKey(
            entity = TrainingPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["training_plan_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("training_plan_id")],
)
data class PlannedDayEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "training_plan_id") val trainingPlanId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "order_in_plan") val order: Int,
)