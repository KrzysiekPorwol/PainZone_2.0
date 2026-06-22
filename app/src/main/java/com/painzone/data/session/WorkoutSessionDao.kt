package com.painzone.data.session

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.painzone.data.session.relation.SessionWithSnapshots
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {

    @Insert
    suspend fun insert(entity: WorkoutSessionEntity): Long

    @Update
    suspend fun update(entity: WorkoutSessionEntity)

    @Query("DELETE FROM workout_session WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM workout_session WHERE id = :id")
    suspend fun getById(id: Long): WorkoutSessionEntity?

    // At most one in-progress session globally (repo enforces on start).
    @Query("SELECT * FROM workout_session WHERE finished_at IS NULL LIMIT 1")
    suspend fun getInProgress(): WorkoutSessionEntity?

    @Query("SELECT * FROM workout_session WHERE finished_at IS NULL LIMIT 1")
    fun observeInProgress(): Flow<WorkoutSessionEntity?>

    @Query("SELECT * FROM workout_session WHERE finished_at IS NOT NULL ORDER BY started_at DESC")
    fun observeCompleted(): Flow<List<WorkoutSessionEntity>>

    @Transaction
    @Query("SELECT * FROM workout_session WHERE id = :id")
    suspend fun getWithSnapshots(id: Long): SessionWithSnapshots?

    @Transaction
    @Query("SELECT * FROM workout_session WHERE id = :id")
    fun observeWithSnapshots(id: Long): Flow<SessionWithSnapshots?>
}
