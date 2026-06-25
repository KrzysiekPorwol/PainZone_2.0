package com.painzone.data.session

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.painzone.data.session.relation.CompletedSessionRow
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

    // S3 hub empty state: true once at least one session has been finished.
    @Query("SELECT EXISTS(SELECT 1 FROM workout_session WHERE finished_at IS NOT NULL)")
    fun observeHasCompleted(): Flow<Boolean>

    // S13 history list: finished sessions newest→oldest with aggregated set count + tonnage.
    // LEFT JOIN keeps sessions that logged nothing. :planNameFilter NULL = no plan filter.
    @Query(
        """
        SELECT s.id AS id,
               s.plan_name_snapshot AS plan_name_snapshot,
               s.day_name_snapshot AS day_name_snapshot,
               s.started_at AS started_at,
               COUNT(ls.id) AS set_count,
               COALESCE(SUM(ls.reps * ls.weight), 0) AS tonnage
        FROM workout_session s
        LEFT JOIN session_exercise_snapshot snap ON snap.session_id = s.id
        LEFT JOIN logged_set ls ON ls.session_exercise_snapshot_id = snap.id
        WHERE s.finished_at IS NOT NULL
          AND (:planNameFilter IS NULL OR s.plan_name_snapshot = :planNameFilter)
        GROUP BY s.id
        ORDER BY s.started_at DESC
        """,
    )
    fun observeCompleted(planNameFilter: String?): Flow<List<CompletedSessionRow>>

    // S12 plan picker: distinct plan names among finished sessions (by snapshot, alphabetical).
    @Query(
        """
        SELECT DISTINCT plan_name_snapshot FROM workout_session
        WHERE finished_at IS NOT NULL
        ORDER BY plan_name_snapshot COLLATE NOCASE
        """,
    )
    fun observeSessionPlanNames(): Flow<List<String>>

    // Rotation anchor for the smart suggestion (S1): plannedDayId of the most recently started
    // session among the given days, or null when none of them has ever been trained.
    @Query(
        """
        SELECT planned_day_id FROM workout_session
        WHERE planned_day_id IN (:dayIds)
        ORDER BY started_at DESC
        LIMIT 1
        """,
    )
    suspend fun lastStartedDayId(dayIds: List<Long>): Long?

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
