package com.painzone.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Route

@Serializable
data object Train : Route

@Serializable
data object Plans : Route

@Serializable
data object Progress : Route
