package com.painzone.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Sticky dark M3 — see ADR-0005. No light scheme, no dynamic color, no toggle.
private val DarkColorScheme = darkColorScheme()

@Composable
fun PainZoneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}