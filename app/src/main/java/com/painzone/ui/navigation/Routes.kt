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
