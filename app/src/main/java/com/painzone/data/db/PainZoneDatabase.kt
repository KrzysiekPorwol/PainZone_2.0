package com.painzone.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.painzone.data.exercise.ExerciseDao
import com.painzone.data.exercise.ExerciseEntity
import com.painzone.data.plan.PlannedDayDao
import com.painzone.data.plan.PlannedDayEntity
import com.painzone.data.plan.PlannedExerciseDao
import com.painzone.data.plan.PlannedExerciseEntity
import com.painzone.data.plan.TrainingPlanDao
import com.painzone.data.plan.TrainingPlanEntity

@Database(
    entities = [
        ExerciseEntity::class,
        TrainingPlanEntity::class,
        PlannedDayEntity::class,
        PlannedExerciseEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class PainZoneDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun trainingPlanDao(): TrainingPlanDao
    abstract fun plannedDayDao(): PlannedDayDao
    abstract fun plannedExerciseDao(): PlannedExerciseDao
}