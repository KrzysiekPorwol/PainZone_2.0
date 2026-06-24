package com.painzone.domain.session

import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    // In-progress session (≤1 globally) — backs resume after pause / process death.
    fun observeInProgress(): Flow<WorkoutSession?>
    suspend fun getInProgress(): WorkoutSession?

    // Full session graph (snapshots + logged sets) for the session screen and resume restore.
    fun observeSessionDetail(sessionId: Long): Flow<SessionDetail?>
    suspend fun getSessionDetail(sessionId: Long): SessionDetail?

    // Smart suggestion rotation anchor (S1): plannedDayId of the most recently started session
    // among the given plan days, or null when none has been trained. Feeds suggestNextDay().
    suspend fun lastStartedDayId(dayIds: List<Long>): Long?

    // Starts a session from a plan day: snapshots plan/day names and creates one
    // SessionExerciseSnapshot per planned exercise (cel z planu). Atomic; enforces ≤1 in-progress.
    suspend fun start(plannedDayId: Long): StartSessionResult

    // Finishes the in-progress session (stamps finishedAt = now → Completed). Allowed mid-plan —
    // an unfinished plan is normal. Idempotent: a finished session stays finished. Once finished
    // the session is read-only — log()/edit() refuse to touch it.
    suspend fun finish(sessionId: Long): FinishSessionResult

    // Pre-fill weight for the input stepper (ciężar z ostatniej sesji): the most recently
    // logged weight for this exercise across prior sessions, or null when there is no history.
    suspend fun lastWeightForExercise(exerciseId: Long): Double?

    // Last Set Preview (S9): the ordered set list this exercise had in its most recent prior
    // session (excludes the current one). Empty when the exercise has no prior session.
    // Index = series number − 1, so series K compares against the same series last time.
    suspend fun lastSessionSetsForExercise(exerciseId: Long, excludingSessionId: Long): List<LastSetPreview>

    // Appends a logged set to the snapshot (order = max+1, completedAt = now). Returns the new id,
    // or -1 when refused because the session is already finished (read-only, M3.10).
    suspend fun log(snapshotId: Long, reps: Int, weight: Double, rpe: Rpe?): Long

    // Overwrites an existing logged set (reps/weight/rpe) — backs "edycja świeżej serii nadpisuje".
    // No-op when the set no longer exists or its session is finished (read-only). Order/completedAt kept.
    suspend fun edit(setId: Long, reps: Int, weight: Double, rpe: Rpe?)
}

data class SessionDetail(
    val session: WorkoutSession,
    val exercises: List<SessionExerciseDetail>,
)

data class SessionExerciseDetail(
    val snapshot: SessionExerciseSnapshot,
    val loggedSets: List<LoggedSet>,
)

sealed interface StartSessionResult {
    data class Success(val sessionId: Long) : StartSessionResult
    data object DayNotFound : StartSessionResult
    data object AlreadyInProgress : StartSessionResult
    data object EmptyDay : StartSessionResult
}

sealed interface FinishSessionResult {
    data object Success : FinishSessionResult
    data object NotFound : FinishSessionResult
    data object AlreadyFinished : FinishSessionResult
}
