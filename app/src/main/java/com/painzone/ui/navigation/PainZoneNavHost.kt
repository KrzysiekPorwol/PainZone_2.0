package com.painzone.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.painzone.ui.library.LibraryScreen
import com.painzone.ui.plans.PlanCreateScreen
import com.painzone.ui.plans.PlansScreen
import androidx.navigation.toRoute
import com.painzone.ui.plans.detail.DayDetailScreen
import com.painzone.ui.plans.detail.ExercisePickerScreen
import com.painzone.ui.plans.detail.PlanDetailScreen
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
                onOpenPlan = { planId -> navController.navigate(PlanDetail(planId)) },
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
        composable<PlanDetail> {
            PlanDetailScreen(
                onBack = { navController.popBackStack() },
                onOpenDay = { dayId, dayName ->
                    navController.navigate(DayDetail(dayId, dayName))
                },
            )
        }
        composable<DayDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<DayDetail>()
            DayDetailScreen(
                dayName = args.dayName,
                onBack = { navController.popBackStack() },
                onAddExercise = { navController.navigate(ExercisePicker(args.dayId)) },
            )
        }
        composable<ExercisePicker> {
            ExercisePickerScreen(
                onBack = { navController.popBackStack() },
                onExerciseAdded = { navController.popBackStack() },
            )
        }
    }
}