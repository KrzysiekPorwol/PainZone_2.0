package com.painzone.domain.session

import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    // In-progress session (≤1 globally) — backs resume after pause / process death.
    fun observeInProgress(): Flow<WorkoutSession?>
    suspend fun getInProgress(): WorkoutSession?

    // Full session graph (snapshots + logged sets) for the session screen and resume restore.
    fun observeSessionDetail(sessionId: Long): Flow<SessionDetail?>
    suspend fun getSessionDetail(sessionId: Long): SessionDetail?

    // Starts a session from a plan day: snapshots plan/day names and creates one
    // SessionExerciseSnapshot per planned exercise (cel z planu). Atomic; enforces ≤1 in-progress.
    suspend fun start(plannedDayId: Long): StartSessionResult

    // Pre-fill weight for the input stepper (ciężar z ostatniej sesji): the most recently
    // logged weight for this exercise across prior sessions, or null when there is no history.
    suspend fun lastWeightForExercise(exerciseId: Long): Double?
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
