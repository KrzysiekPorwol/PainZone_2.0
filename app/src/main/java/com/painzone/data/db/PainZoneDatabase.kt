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
import com.painzone.data.session.LoggedSetDao
import com.painzone.data.session.LoggedSetEntity
import com.painzone.data.session.SessionExerciseSnapshotDao
import com.painzone.data.session.SessionExerciseSnapshotEntity
import com.painzone.data.session.WorkoutSessionDao
import com.painzone.data.session.WorkoutSessionEntity
import com.painzone.data.stats.StatsDao

@Database(
    entities = [
        ExerciseEntity::class,
        TrainingPlanEntity::class,
        PlannedDayEntity::class,
        PlannedExerciseEntity::class,
        WorkoutSessionEntity::class,
        SessionExerciseSnapshotEntity::class,
        LoggedSetEntity::class,
    ],
    version = 5,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class PainZoneDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun trainingPlanDao(): TrainingPlanDao
    abstract fun plannedDayDao(): PlannedDayDao
    abstract fun plannedExerciseDao(): PlannedExerciseDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun sessionExerciseSnapshotDao(): SessionExerciseSnapshotDao
    abstract fun loggedSetDao(): LoggedSetDao
    abstract fun statsDao(): StatsDao
}