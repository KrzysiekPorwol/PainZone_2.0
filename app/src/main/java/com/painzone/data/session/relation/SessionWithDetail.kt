package com.painzone.data.session.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.painzone.data.session.SessionExerciseSnapshotEntity
import com.painzone.data.session.WorkoutSessionEntity

// Full session graph in one read: session + its snapshots, each with its logged sets.
// Backs session-screen render and pause/resume restore after process death.
data class SessionWithDetail(
    @Embedded val session: WorkoutSessionEntity,
    @Relation(
        entity = SessionExerciseSnapshotEntity::class,
        parentColumn = "id",
        entityColumn = "session_id",
    )
    val snapshots: List<SnapshotWithLoggedSets>,
)
