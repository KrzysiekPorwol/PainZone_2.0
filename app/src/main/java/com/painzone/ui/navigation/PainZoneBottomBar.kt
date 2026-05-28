package com.painzone.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.painzone.ui.theme.PainZoneTheme
import kotlin.reflect.KClass

private data class TopLevelTab(
    val route: Route,
    val routeClass: KClass<out Route>,
    val label: String,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector,
)

private val topLevelTabs = listOf(
    TopLevelTab(
        route = Train,
        routeClass = Train::class,
        label = "Trenuj",
        iconSelected = Icons.Filled.FitnessCenter,
        iconUnselected = Icons.Outlined.FitnessCenter,
    ),
    TopLevelTab(
        route = Plans,
        routeClass = Plans::class,
        label = "Plany",
        iconSelected = Icons.AutoMirrored.Filled.ListAlt,
        iconUnselected = Icons.AutoMirrored.Outlined.ListAlt,
    ),
    TopLevelTab(
        route = Progress,
        routeClass = Progress::class,
        label = "Postęp",
        iconSelected = Icons.AutoMirrored.Filled.TrendingUp,
        iconUnselected = Icons.AutoMirrored.Outlined.TrendingUp,
    ),
)

@Composable
fun PainZoneBottomBar(navController: NavHostController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    PainZoneBottomBarContent(
        currentDestination = currentDestination,
        onTabClick = { tab ->
            // Drop any non-tab destination (e.g. Library) without saving its state,
            // so it isn't restored as part of the underlying tab's back stack.
            val onTab = topLevelTabs.any { t ->
                currentDestination?.hierarchy?.any { it.hasRoute(t.routeClass) } == true
            }
            if (!onTab) {
                navController.popBackStack(
                    navController.graph.findStartDestination().id,
                    inclusive = false,
                    saveState = false,
                )
            }
            navController.navigate(tab.route) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        },
    )
}

@Composable
private fun PainZoneBottomBarContent(
    currentDestination: NavDestination?,
    onTabClick: (TopLevelTab) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        )
        NavigationBar(
            modifier = Modifier.height(64.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0),
        ) {
            topLevelTabs.forEach { tab ->
                val selected = currentDestination?.hierarchy
                    ?.any { it.hasRoute(tab.routeClass) } == true
                NavigationBarItem(
                    selected = selected,
                    onClick = { onTabClick(tab) },
                    icon = {
                        Icon(
                            imageVector = if (selected) tab.iconSelected else tab.iconUnselected,
                            contentDescription = tab.label,
                        )
                    },
                    label = {
                        Text(
                            text = tab.label,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent,
                    ),
                )
            }
        }
    }
}

@Preview
@Composable
private fun PainZoneBottomBarTrainSelectedPreview() {
    PainZoneTheme {
        PainZoneBottomBar(rememberNavController())
    }
}