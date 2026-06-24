package com.painzone.domain.stats

import kotlinx.coroutines.flow.Flow

interface StatsRepository {

    // Stats Lite (S10): every logged set this exercise has across *finished* sessions, within the
    // period window, newest session first then by series order. Joined via the exercise snapshot,
    // so a soft-deleted exercise still surfaces its history (M4.5). The in-progress session is
    // excluded — stats is historical. Empty when the exercise has no logged history in the window.
    fun observeSets(exerciseId: Long, period: StatsPeriod): Flow<List<StatsSet>>
}
