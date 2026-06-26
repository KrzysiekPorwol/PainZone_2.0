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

    @Query("SELECT * FROM exercise WHERE id = :id")
    fun observeById(id: Long): Flow<ExerciseEntity?>

    // Soft-deleted exercise ids — backs the per-exercise "usunięte" marker on S14 (read-only
    // session detail). Bounded set: only deleted exercises, of which there are few.
    @Query("SELECT id FROM exercise WHERE deleted_at IS NOT NULL")
    fun observeDeletedIds(): Flow<List<Long>>

    @Query("SELECT * FROM exercise WHERE name = :name AND deleted_at IS NULL LIMIT 1")
    suspend fun findActiveByName(name: String): ExerciseEntity?
}
