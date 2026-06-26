package com.painzone.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
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
import com.painzone.ui.history.SessionDetailScreen
import com.painzone.ui.history.SessionHistoryScreen
import com.painzone.ui.history.SessionPlanPickerScreen
import com.painzone.ui.progress.ProgressByExerciseScreen
import com.painzone.ui.progress.ProgressHubScreen
import com.painzone.ui.session.SessionScreen
import com.painzone.ui.stats.StatsExerciseScreen
import com.painzone.ui.train.TrainScreen

@Composable
fun PainZoneNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val onManageLibrary: () -> Unit = { navController.navigate(Library) }
    // Switch to the Plans tab with the same semantics as a bottom-bar tap.
    val onGoToPlans: () -> Unit = {
        navController.navigate(Plans) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    NavHost(
        navController = navController,
        startDestination = Train,
        modifier = modifier,
    ) {
        composable<Train> {
            TrainScreen(
                onManageLibrary = onManageLibrary,
                onGoToPlans = onGoToPlans,
                onOpenSession = { sessionId -> navController.navigate(Session(sessionId)) },
            )
        }
        composable<Plans> {
            PlansScreen(
                onManageLibrary = onManageLibrary,
                onCreatePlan = { navController.navigate(PlanCreate) },
                onOpenPlan = { planId -> navController.navigate(PlanDetail(planId)) },
            )
        }
        composable<Progress> {
            ProgressHubScreen(
                onManageLibrary = onManageLibrary,
                onByExercise = { navController.navigate(ProgressByExercise) },
                onByPlan = { navController.navigate(SessionPlanPicker) },
                onChronological = { navController.navigate(SessionHistory()) },
            )
        }
        composable<ProgressByExercise> {
            ProgressByExerciseScreen(
                onBack = { navController.popBackStack() },
                onOpenStats = { exerciseId, name, muscleGroupLabel ->
                    navController.navigate(StatsExercise(exerciseId, name, muscleGroupLabel))
                },
            )
        }
        composable<SessionPlanPicker> {
            SessionPlanPickerScreen(
                onBack = { navController.popBackStack() },
                onSelectPlan = { planName -> navController.navigate(SessionHistory(planName)) },
            )
        }
        composable<SessionHistory> {
            SessionHistoryScreen(
                onBack = { navController.popBackStack() },
                onOpenSession = { sessionId -> navController.navigate(SessionDetail(sessionId)) },
            )
        }
        composable<SessionDetail> {
            SessionDetailScreen(onBack = { navController.popBackStack() })
        }
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
        composable<Session> {
            SessionScreen(onExit = { navController.popBackStack() })
        }
        composable<StatsExercise> { backStackEntry ->
            val args = backStackEntry.toRoute<StatsExercise>()
            StatsExerciseScreen(
                exerciseName = args.exerciseName,
                muscleGroupLabel = args.muscleGroupLabel,
                onBack = { navController.popBackStack() },
            )
        }
    }
}