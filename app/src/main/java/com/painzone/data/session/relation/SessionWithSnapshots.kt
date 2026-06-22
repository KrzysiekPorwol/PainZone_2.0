package com.painzone.data.session.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.painzone.data.session.SessionExerciseSnapshotEntity
import com.painzone.data.session.WorkoutSessionEntity

data class SessionWithSnapshots(
    @Embedded val session: WorkoutSessionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "session_id",
    )
    val snapshots: List<SessionExerciseSnapshotEntity>,
)
