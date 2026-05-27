package com.painzone.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.painzone.ui.plans.PlansScreen
import com.painzone.ui.progress.ProgressScreen
import com.painzone.ui.train.TrainScreen

@Composable
fun PainZoneNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Train,
        modifier = modifier,
    ) {
        composable<Train> { TrainScreen() }
        composable<Plans> { PlansScreen() }
        composable<Progress> { ProgressScreen() }
    }
}