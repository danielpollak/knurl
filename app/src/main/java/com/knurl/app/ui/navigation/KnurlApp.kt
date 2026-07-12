package com.knurl.app.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.knurl.app.ui.exercises.ExercisesScreen
import com.knurl.app.ui.history.HistoryScreen
import com.knurl.app.ui.history.SessionDetailScreen
import com.knurl.app.ui.programs.ProgramEditorScreen
import com.knurl.app.ui.programs.ProgramsScreen
import com.knurl.app.ui.workout.WorkoutScreen

private const val SESSION_DETAIL_ROUTE = "history/{sessionId}"
private const val PROGRAM_EDITOR_ROUTE = "programs/editor"

@Composable
fun KnurlApp() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.hierarchy?.map { it.route }
            NavigationBar {
                KnurlDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute?.contains(destination.route) == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = KnurlDestination.WORKOUT.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(KnurlDestination.WORKOUT.route) { WorkoutScreen() }

            composable(KnurlDestination.HISTORY.route) {
                HistoryScreen(onSessionClick = { sessionId -> navController.navigate("history/$sessionId") })
            }
            composable(SESSION_DETAIL_ROUTE) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId")?.toLongOrNull()
                if (sessionId != null) SessionDetailScreen(sessionId = sessionId)
            }

            composable(KnurlDestination.PROGRAMS.route) {
                ProgramsScreen(onCreateCustom = { navController.navigate(PROGRAM_EDITOR_ROUTE) })
            }
            composable(PROGRAM_EDITOR_ROUTE) {
                ProgramEditorScreen(onSaved = { navController.popBackStack() })
            }

            composable(KnurlDestination.EXERCISES.route) { ExercisesScreen() }
        }
    }
}
