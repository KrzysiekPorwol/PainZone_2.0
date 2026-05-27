package com.painzone.data.exercise

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.painzone.domain.exercise.MuscleGroup
import java.time.Instant

@Entity(tableName = "exercise")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "muscle_group") val muscleGroup: MuscleGroup,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
    @ColumnInfo(name = "deleted_at") val deletedAt: Instant? = null,
)