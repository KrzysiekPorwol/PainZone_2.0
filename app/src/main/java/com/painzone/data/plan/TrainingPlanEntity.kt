package com.painzone.data.plan

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "training_plan")
data class TrainingPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
    // PlanIcon enum stored by name; defaulted for rows created before M5 (icon feature).
    @ColumnInfo(name = "icon", defaultValue = "FITNESS_CENTER") val icon: String = "FITNESS_CENTER",
)