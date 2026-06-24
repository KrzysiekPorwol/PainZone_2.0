package com.painzone.data.di

import com.painzone.data.exercise.ExerciseRepositoryImpl
import com.painzone.data.plan.PlanRepositoryImpl
import com.painzone.data.session.SessionRepositoryImpl
import com.painzone.data.stats.StatsRepositoryImpl
import com.painzone.domain.exercise.ExerciseRepository
import com.painzone.domain.plan.PlanRepository
import com.painzone.domain.session.SessionRepository
import com.painzone.domain.stats.StatsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExerciseRepository(impl: ExerciseRepositoryImpl): ExerciseRepository

    @Binds
    @Singleton
    abstract fun bindPlanRepository(impl: PlanRepositoryImpl): PlanRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

    @Binds
    @Singleton
    abstract fun bindStatsRepository(impl: StatsRepositoryImpl): StatsRepository
}