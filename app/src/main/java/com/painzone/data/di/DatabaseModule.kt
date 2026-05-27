package com.painzone.data.di

import android.content.Context
import androidx.room.Room
import com.painzone.data.db.PainZoneDatabase
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
        ).build()
}