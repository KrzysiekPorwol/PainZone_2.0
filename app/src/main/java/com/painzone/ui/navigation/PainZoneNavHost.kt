package com.painzone.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.painzone.ui.library.LibraryScreen
import com.painzone.ui.plans.PlansScreen
import com.painzone.ui.progress.ProgressScreen
import com.painzone.ui.train.TrainScreen

@Composable
fun PainZoneNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val onManageLibrary: () -> Unit = { navController.navigate(Library) }
    NavHost(
        navController = navController,
        startDestination = Train,
        modifier = modifier,
    ) {
        composable<Train> { TrainScreen(onManageLibrary = onManageLibrary) }
        composable<Plans> { PlansScreen(onManageLibrary = onManageLibrary) }
        composable<Progress> { ProgressScreen(onManageLibrary = onManageLibrary) }
        composable<Library> { LibraryScreen(onBack = { navController.popBackStack() }) }
    }
}