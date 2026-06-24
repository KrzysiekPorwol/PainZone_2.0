package com.painzone.data.stats

import com.painzone.domain.stats.StatsPeriod
import com.painzone.domain.stats.StatsRepository
import com.painzone.domain.stats.StatsSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsRepositoryImpl @Inject constructor(
    private val statsDao: StatsDao,
) : StatsRepository {

    override fun observeSets(exerciseId: Long, period: StatsPeriod): Flow<List<StatsSet>> {
        // Window boundary is fixed when the flow is built — stats data doesn't change while viewed,
        // and switching period (M4.3) resubscribes with a fresh `now`.
        val since = period.since(Instant.now())
        return statsDao.observeSetsForExercise(exerciseId, since).map { rows ->
            rows.map { it.toDomain() }
        }
    }
}
