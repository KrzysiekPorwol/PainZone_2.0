package com.painzone.data.session

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.painzone.data.session.relation.SnapshotWithLoggedSets
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionExerciseSnapshotDao {

    @Insert
    suspend fun insert(entity: SessionExerciseSnapshotEntity): Long

    // Created in bulk by WorkoutSession.start() inside one transaction.
    @Insert
    suspend fun insertAll(entities: List<SessionExerciseSnapshotEntity>): List<Long>

    @Query("SELECT * FROM session_exercise_snapshot WHERE id = :id")
    suspend fun getById(id: Long): SessionExerciseSnapshotEntity?

    @Query("SELECT * FROM session_exercise_snapshot WHERE session_id = :sessionId ORDER BY order_in_session")
    fun observeBySessionId(sessionId: Long): Flow<List<SessionExerciseSnapshotEntity>>

    @Transaction
    @Query("SELECT * FROM session_exercise_snapshot WHERE id = :id")
    suspend fun getWithLoggedSets(id: Long): SnapshotWithLoggedSets?

    @Transaction
    @Query("SELECT * FROM session_exercise_snapshot WHERE session_id = :sessionId ORDER BY order_in_session")
    fun observeWithLoggedSetsBySessionId(sessionId: Long): Flow<List<SnapshotWithLoggedSets>>
}
