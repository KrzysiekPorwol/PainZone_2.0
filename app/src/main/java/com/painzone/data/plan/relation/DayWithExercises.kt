package com.painzone.data.plan.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.painzone.data.plan.PlannedDayEntity
import com.painzone.data.plan.PlannedExerciseEntity

data class DayWithExercises(
    @Embedded val day: PlannedDayEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "planned_day_id",
    )
    val exercises: List<PlannedExerciseEntity>,
)