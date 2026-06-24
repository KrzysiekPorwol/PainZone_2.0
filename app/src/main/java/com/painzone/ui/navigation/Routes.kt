package com.painzone.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Route

@Serializable
data object Train : Route

@Serializable
data object Plans : Route

@Serializable
data object Progress : Route

@Serializable
data object Library : Route

@Serializable
data object PlanCreate : Route

@Serializable
data class PlanDetail(val planId: Long) : Route

// dayName carried in the route: rename is deferred (M2.6 minimal scope), so the
// title is stable and we avoid a single-day observe query in the repository.
@Serializable
data class DayDetail(val dayId: Long, val dayName: String) : Route

@Serializable
data class ExercisePicker(val dayId: Long) : Route

// S9 — active workout session. Focus mode: bottom bar hidden while on this route.
@Serializable
data class Session(val sessionId: Long) : Route

// S10 — Stats Lite for one exercise. Name + muscle group label are carried in the route
// (like DayDetail) so the title is stable and survives a soft-deleted exercise (M4.5),
// avoiding a lookup query that wouldn't find a deleted one.
@Serializable
data class StatsExercise(
    val exerciseId: Long,
    val exerciseName: String,
    val muscleGroupLabel: String,
) : Route
