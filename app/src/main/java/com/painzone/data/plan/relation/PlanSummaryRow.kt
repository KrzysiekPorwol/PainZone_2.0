package com.painzone.data.plan.relation

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.painzone.data.plan.TrainingPlanEntity

// Projection for the plans list: plan row + derived day count from a subquery.
data class PlanSummaryRow(
    @Embedded val plan: TrainingPlanEntity,
    @ColumnInfo(name = "day_count") val dayCount: Int,
)