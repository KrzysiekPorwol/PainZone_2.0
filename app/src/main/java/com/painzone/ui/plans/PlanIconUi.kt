package com.painzone.ui.plans

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.ui.graphics.vector.ImageVector
import com.painzone.domain.plan.PlanIcon

// UI-only bridge: maps the domain PlanIcon to a Material icon + Polish label.
// Keeps Compose deps out of the domain layer.
fun PlanIcon.toImageVector(): ImageVector = when (this) {
    PlanIcon.FITNESS_CENTER -> Icons.Filled.FitnessCenter
    PlanIcon.DIRECTIONS_RUN -> Icons.AutoMirrored.Filled.DirectionsRun
    PlanIcon.SELF_IMPROVEMENT -> Icons.Filled.SelfImprovement
    PlanIcon.SPORTS_GYMNASTICS -> Icons.Filled.SportsGymnastics
    PlanIcon.SPORTS_MARTIAL_ARTS -> Icons.Filled.SportsMartialArts
    PlanIcon.DIRECTIONS_BIKE -> Icons.AutoMirrored.Filled.DirectionsBike
    PlanIcon.POOL -> Icons.Filled.Pool
    PlanIcon.HIKING -> Icons.Filled.Hiking
    PlanIcon.MONITOR_HEART -> Icons.Filled.MonitorHeart
    PlanIcon.BOLT -> Icons.Filled.Bolt
    PlanIcon.FAVORITE -> Icons.Filled.Favorite
    PlanIcon.SPORTS_SCORE -> Icons.Filled.SportsScore
}

fun PlanIcon.contentLabel(): String = when (this) {
    PlanIcon.FITNESS_CENTER -> "Hantel"
    PlanIcon.DIRECTIONS_RUN -> "Bieganie"
    PlanIcon.SELF_IMPROVEMENT -> "Joga"
    PlanIcon.SPORTS_GYMNASTICS -> "Gimnastyka"
    PlanIcon.SPORTS_MARTIAL_ARTS -> "Sztuki walki"
    PlanIcon.DIRECTIONS_BIKE -> "Rower"
    PlanIcon.POOL -> "Pływanie"
    PlanIcon.HIKING -> "Wędrówka"
    PlanIcon.MONITOR_HEART -> "Cardio"
    PlanIcon.BOLT -> "Moc"
    PlanIcon.FAVORITE -> "Serce"
    PlanIcon.SPORTS_SCORE -> "Cel"
}
