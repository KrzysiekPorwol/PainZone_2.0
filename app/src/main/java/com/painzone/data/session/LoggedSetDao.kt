package com.painzone.data.session

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LoggedSetDao {

    @Insert
    suspend fun insert(entity: LoggedSetEntity): Long

    @Update
    suspend fun update(entity: LoggedSetEntity)

    @Query("DELETE FROM logged_set WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM logged_set WHERE id = :id")
    suspend fun getById(id: Long): LoggedSetEntity?

    @Query("SELECT * FROM logged_set WHERE session_exercise_snapshot_id = :snapshotId ORDER BY order_in_exercise")
    fun observeBySnapshotId(snapshotId: Long): Flow<List<LoggedSetEntity>>

    @Query("SELECT * FROM logged_set WHERE session_exercise_snapshot_id = :snapshotId ORDER BY order_in_exercise")
    suspend fun getBySnapshotId(snapshotId: Long): List<LoggedSetEntity>

    @Query("SELECT MAX(order_in_exercise) FROM logged_set WHERE session_exercise_snapshot_id = :snapshotId")
    suspend fun maxOrderInSnapshot(snapshotId: Long): Int?

    // completedAt of the freshest set in this exercise — the rest clock's start for the next set.
    // Null when no set is logged yet (first set has no rest before it).
    @Query(
        """
        SELECT completed_at FROM logged_set
        WHERE session_exercise_snapshot_id = :snapshotId
        ORDER BY order_in_exercise DESC
        LIMIT 1
        """,
    )
    suspend fun lastCompletedAtInSnapshot(snapshotId: Long): java.time.Instant?

    // Pre-fill source: weight of the most recently logged set of this exercise across
    // all sessions (joined via snapshot.exercise_id). Null when the exercise has no history.
    @Query(
        """
        SELECT ls.weight FROM logged_set ls
        JOIN session_exercise_snapshot s ON ls.session_exercise_snapshot_id = s.id
        WHERE s.exercise_id = :exerciseId
        ORDER BY ls.completed_at DESC
        LIMIT 1
        """,
    )
    suspend fun lastWeightForExercise(exerciseId: Long): Double?

    // Last Set Preview source: id of this exercise's snapshot in the most recent *prior* session
    // (the one holding the set with the latest completed_at). Null when there is no prior history.
    // Its full ordered set list (getBySnapshotId) backs per-series comparison — series K shows
    // what was logged for series K last time, not the chronologically last (weakest) set.
    @Query(
        """
        SELECT s.id FROM session_exercise_snapshot s
        JOIN logged_set ls ON ls.session_exercise_snapshot_id = s.id
        WHERE s.exercise_id = :exerciseId AND s.session_id != :excludingSessionId
        ORDER BY ls.completed_at DESC
        LIMIT 1
        """,
    )
    suspend fun lastSessionSnapshotIdForExercise(exerciseId: Long, excludingSessionId: Long): Long?

    @Query("UPDATE logged_set SET order_in_exercise = :order WHERE id = :id")
    suspend fun updateOrder(id: Long, order: Int)

    // Re-sequence after a delete so order stays 1..n contiguous within the exercise.
    @Transaction
    suspend fun resequence(snapshotId: Long) {
        getBySnapshotId(snapshotId).forEachIndexed { index, set -> updateOrder(set.id, index + 1) }
    }
}
