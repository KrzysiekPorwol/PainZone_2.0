package com.painzone.data.session

import com.painzone.data.plan.PlannedDayDao
import com.painzone.data.plan.PlannedExerciseDao
import com.painzone.data.plan.TrainingPlanDao
import com.painzone.data.exercise.ExerciseDao
import com.painzone.data.session.relation.SessionWithDetail
import com.painzone.domain.session.SessionDetail
import com.painzone.domain.session.SessionExerciseDetail
import com.painzone.domain.session.SessionExerciseSnapshot
import com.painzone.domain.session.SessionRepository
import com.painzone.domain.session.StartSessionResult
import com.painzone.domain.session.WorkoutSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: WorkoutSessionDao,
    private val snapshotDao: SessionExerciseSnapshotDao,
    private val loggedSetDao: LoggedSetDao,
    private val dayDao: PlannedDayDao,
    private val planDao: TrainingPlanDao,
    private val itemDao: PlannedExerciseDao,
    private val exerciseDao: ExerciseDao,
) : SessionRepository {

    override fun observeInProgress(): Flow<WorkoutSession?> =
        sessionDao.observeInProgress().map { it?.toDomain() }

    override suspend fun getInProgress(): WorkoutSession? =
        sessionDao.getInProgress()?.toDomain()

    override fun observeSessionDetail(sessionId: Long): Flow<SessionDetail?> =
        sessionDao.observeWithDetail(sessionId).map { it?.toDomain() }

    override suspend fun getSessionDetail(sessionId: Long): SessionDetail? =
        sessionDao.getWithDetail(sessionId)?.toDomain()

    override suspend fun start(plannedDayId: Long): StartSessionResult {
        if (sessionDao.getInProgress() != null) return StartSessionResult.AlreadyInProgress
        val day = dayDao.getById(plannedDayId) ?: return StartSessionResult.DayNotFound
        // Day without a plan should be impossible (FK CASCADE), but guard rather than crash.
        val plan = planDao.getById(day.trainingPlanId) ?: return StartSessionResult.DayNotFound
        val items = itemDao.getByDayId(plannedDayId)
        if (items.isEmpty()) return StartSessionResult.EmptyDay

        val now = Instant.now()
        val session = WorkoutSession.create(
            plannedDayId = plannedDayId,
            planNameSnapshot = plan.name,
            dayNameSnapshot = day.name,
            startedAt = now,
        ).toEntity()

        // Snapshot each planned exercise. exerciseDao.getById returns soft-deleted rows too,
        // so the snapshot freezes name/muscleGroup regardless of deletion state. Order is
        // re-indexed over successfully resolved exercises to stay contiguous from 0.
        val snapshots = items
            .mapNotNull { item -> exerciseDao.getById(item.exerciseId)?.let { item to it } }
            .mapIndexed { index, (item, exercise) ->
                SessionExerciseSnapshot.create(
                    sessionId = 0L, // stamped inside startWithSnapshots
                    exerciseId = item.exerciseId,
                    exerciseNameSnapshot = exercise.name,
                    muscleGroupSnapshot = exercise.muscleGroup,
                    order = index,
                    plannedTargetReps = item.targetReps,
                    plannedRestSeconds = item.restSeconds,
                ).toEntity()
            }
        if (snapshots.isEmpty()) return StartSessionResult.EmptyDay

        val sessionId = sessionDao.startWithSnapshots(session, snapshots)
        return StartSessionResult.Success(sessionId)
    }

    override suspend fun lastWeightForExercise(exerciseId: Long): Double? =
        loggedSetDao.lastWeightForExercise(exerciseId)

    private fun SessionWithDetail.toDomain(): SessionDetail = SessionDetail(
        session = session.toDomain(),
        exercises = snapshots
            .sortedBy { it.snapshot.order }
            .map { swl ->
                SessionExerciseDetail(
                    snapshot = swl.snapshot.toDomain(),
                    loggedSets = swl.loggedSets
                        .sortedBy { it.order }
                        .map { it.toDomain() },
                )
            },
    )
}
