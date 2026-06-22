package com.painzone.data.session.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.painzone.data.session.LoggedSetEntity
import com.painzone.data.session.SessionExerciseSnapshotEntity

data class SnapshotWithLoggedSets(
    @Embedded val snapshot: SessionExerciseSnapshotEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "session_exercise_snapshot_id",
    )
    val loggedSets: List<LoggedSetEntity>,
)
