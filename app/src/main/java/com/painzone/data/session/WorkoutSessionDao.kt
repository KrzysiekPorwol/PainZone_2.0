package com.painzone.data.session

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.painzone.data.session.relation.SessionWithDetail
import com.painzone.data.session.relation.SessionWithSnapshots
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {

    @Insert
    suspend fun insert(entity: WorkoutSessionEntity): Long

    // Snapshots live in their own table but are written here so session + snapshots
    // share one transaction in startWithSnapshots (the session aggregate boundary).
    @Insert
    suspend fun insertSnapshots(entities: List<SessionExerciseSnapshotEntity>): List<Long>

    // Atomic start: insert the session, then its exercise snapshots stamped with the
    // generated session id. All-or-nothing so a half-created session never exists.
    @Transaction
    suspend fun startWithSnapshots(
        session: WorkoutSessionEntity,
        snapshots: List<SessionExerciseSnapshotEntity>,
    ): Long {
        val sessionId = insert(session)
        insertSnapshots(snapshots.map { it.copy(sessionId = sessionId) })
        return sessionId
    }

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

    // Read-only guard (M3.10): true while the snapshot's session is still in progress.
    // A finished session is read-only, so log() refuses to append to it.
    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM session_exercise_snapshot snap
            JOIN workout_session s ON s.id = snap.session_id
            WHERE snap.id = :snapshotId AND s.finished_at IS NULL
        )
        """,
    )
    suspend fun isSnapshotInProgress(snapshotId: Long): Boolean

    // Read-only guard (M3.10): true while the set's owning session is still in progress,
    // so edit() refuses to overwrite a set once the session is finished.
    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM logged_set ls
            JOIN session_exercise_snapshot snap ON snap.id = ls.session_exercise_snapshot_id
            JOIN workout_session s ON s.id = snap.session_id
            WHERE ls.id = :setId AND s.finished_at IS NULL
        )
        """,
    )
    suspend fun isSetInProgress(setId: Long): Boolean

    @Transaction
    @Query("SELECT * FROM workout_session WHERE id = :id")
    suspend fun getWithSnapshots(id: Long): SessionWithSnapshots?

    @Transaction
    @Query("SELECT * FROM workout_session WHERE id = :id")
    fun observeWithSnapshots(id: Long): Flow<SessionWithSnapshots?>

    @Transaction
    @Query("SELECT * FROM workout_session WHERE id = :id")
    suspend fun getWithDetail(id: Long): SessionWithDetail?

    @Transaction
    @Query("SELECT * FROM workout_session WHERE id = :id")
    fun observeWithDetail(id: Long): Flow<SessionWithDetail?>
}
