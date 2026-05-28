package com.painzone.data.plan.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.painzone.data.plan.PlannedDayEntity
import com.painzone.data.plan.TrainingPlanEntity

data class PlanWithDays(
    @Embedded val plan: TrainingPlanEntity,
    @Relation(
        entity = PlannedDayEntity::class,
        parentColumn = "id",
        entityColumn = "training_plan_id",
    )
    val days: List<DayWithExercises>,
)