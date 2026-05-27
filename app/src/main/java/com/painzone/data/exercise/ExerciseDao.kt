package com.painzone.data.exercise

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Insert
    suspend fun insert(entity: ExerciseEntity): Long

    @Update
    suspend fun update(entity: ExerciseEntity)

    @Query("SELECT * FROM exercise WHERE deleted_at IS NULL ORDER BY name COLLATE NOCASE")
    fun observeActive(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercise WHERE id = :id")
    suspend fun getById(id: Long): ExerciseEntity?

    @Query("SELECT * FROM exercise WHERE name = :name AND deleted_at IS NULL LIMIT 1")
    suspend fun findActiveByName(name: String): ExerciseEntity?
}
