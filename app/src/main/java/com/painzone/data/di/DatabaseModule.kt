package com.painzone.data.di

import android.content.Context
import androidx.room.Room
import com.painzone.data.db.MIGRATION_1_2
import com.painzone.data.db.MIGRATION_2_3
import com.painzone.data.db.MIGRATION_3_4
import com.painzone.data.db.PainZoneDatabase
import com.painzone.data.exercise.ExerciseDao
import com.painzone.data.plan.PlannedDayDao
import com.painzone.data.plan.PlannedExerciseDao
import com.painzone.data.plan.TrainingPlanDao
import com.painzone.data.session.LoggedSetDao
import com.painzone.data.session.SessionExerciseSnapshotDao
import com.painzone.data.session.WorkoutSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providePainZoneDatabase(@ApplicationContext context: Context): PainZoneDatabase =
        Room.databaseBuilder(
            context,
            PainZoneDatabase::class.java,
            "pz_db",
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .build()

    @Provides
    fun provideExerciseDao(db: PainZoneDatabase): ExerciseDao = db.exerciseDao()

    @Provides
    fun provideTrainingPlanDao(db: PainZoneDatabase): TrainingPlanDao = db.trainingPlanDao()

    @Provides
    fun providePlannedDayDao(db: PainZoneDatabase): PlannedDayDao = db.plannedDayDao()

    @Provides
    fun providePlannedExerciseDao(db: PainZoneDatabase): PlannedExerciseDao = db.plannedExerciseDao()

    @Provides
    fun provideWorkoutSessionDao(db: PainZoneDatabase): WorkoutSessionDao = db.workoutSessionDao()

    @Provides
    fun provideSessionExerciseSnapshotDao(db: PainZoneDatabase): SessionExerciseSnapshotDao =
        db.sessionExerciseSnapshotDao()

    @Provides
    fun provideLoggedSetDao(db: PainZoneDatabase): LoggedSetDao = db.loggedSetDao()
}