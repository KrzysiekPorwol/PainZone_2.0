package com.painzone.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.painzone.data.exercise.ExerciseDao
import com.painzone.data.exercise.ExerciseEntity

@Database(
    entities = [ExerciseEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class PainZoneDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
}