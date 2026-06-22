package com.painzone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.painzone.ui.navigation.PainZoneBottomBar
import com.painzone.ui.navigation.PainZoneNavHost
import com.painzone.ui.navigation.Session
import com.painzone.ui.theme.PainZoneTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PainZoneTheme {
                val navController = rememberNavController()
                // S9 session is a focus mode — hide the global bottom bar while on it.
                val currentDestination =
                    navController.currentBackStackEntryAsState().value?.destination
                val showBottomBar = currentDestination?.hasRoute<Session>() != true
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { if (showBottomBar) PainZoneBottomBar(navController) },
                ) { innerPadding ->
                    PainZoneNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}