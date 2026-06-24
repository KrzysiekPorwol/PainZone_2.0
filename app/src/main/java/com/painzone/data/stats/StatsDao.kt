package com.painzone.data.stats

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface StatsDao {

    // Logged sets of one exercise across finished sessions, filtered by session date. Joined
    // through the snapshot (exercise_id) so history survives a soft-deleted exercise. `since`
    // null = no lower bound (StatsPeriod.ALL). Newest session first, then series order within it.
    @Query(
        """
        SELECT
            ls.id AS set_id,
            snap.session_id AS session_id,
            ws.started_at AS session_started_at,
            ws.plan_name_snapshot AS plan_name_snapshot,
            ws.day_name_snapshot AS day_name_snapshot,
            ls.order_in_exercise AS order_in_exercise,
            ls.reps AS reps,
            ls.weight AS weight,
            ls.rpe AS rpe,
            ls.rest_before_seconds AS rest_before_seconds,
            ls.completed_at AS completed_at
        FROM logged_set ls
        JOIN session_exercise_snapshot snap ON ls.session_exercise_snapshot_id = snap.id
        JOIN workout_session ws ON ws.id = snap.session_id
        WHERE snap.exercise_id = :exerciseId
          AND ws.finished_at IS NOT NULL
          AND (:since IS NULL OR ws.started_at >= :since)
        ORDER BY ws.started_at DESC, ls.order_in_exercise ASC
        """,
    )
    fun observeSetsForExercise(exerciseId: Long, since: Instant?): Flow<List<StatsSetRow>>
}
