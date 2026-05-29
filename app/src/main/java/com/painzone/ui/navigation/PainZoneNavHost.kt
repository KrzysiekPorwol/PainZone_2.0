package com.painzone.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.painzone.ui.library.LibraryScreen
import com.painzone.ui.plans.PlanCreateScreen
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
        composable<Plans> {
            PlansScreen(
                onManageLibrary = onManageLibrary,
                onCreatePlan = { navController.navigate(PlanCreate) },
                // TODO M2.6: navigate to PlanDetailScreen (S4).
                onOpenPlan = {},
            )
        }
        composable<Progress> { ProgressScreen(onManageLibrary = onManageLibrary) }
        composable<Library> { LibraryScreen(onBack = { navController.popBackStack() }) }
        composable<PlanCreate> {
            PlanCreateScreen(
                onSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
            )
        }
    }
}