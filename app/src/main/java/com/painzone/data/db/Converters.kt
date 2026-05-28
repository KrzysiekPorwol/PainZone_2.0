package com.painzone.data.db

import androidx.room.TypeConverter
import com.painzone.domain.exercise.MuscleGroup
import com.painzone.domain.session.Rpe
import java.time.Instant

object Converters {
    @TypeConverter
    fun instantToEpochMillis(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun epochMillisToInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun muscleGroupToName(value: MuscleGroup?): String? = value?.name

    @TypeConverter
    fun nameToMuscleGroup(value: String?): MuscleGroup? = value?.let(MuscleGroup::valueOf)

    @TypeConverter
    fun rpeToInt(value: Rpe?): Int? = value?.intValue

    @TypeConverter
    fun intToRpe(value: Int?): Rpe? = value?.let(Rpe::fromIntValue)

    @TypeConverter
    fun intListToCsv(value: List<Int>?): String? = value?.joinToString(separator = ",")

    @TypeConverter
    fun csvToIntList(value: String?): List<Int>? =
        value?.let { if (it.isEmpty()) emptyList() else it.split(",").map(String::toInt) }
}