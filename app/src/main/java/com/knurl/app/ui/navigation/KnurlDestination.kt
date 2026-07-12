package com.knurl.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class KnurlDestination(val route: String, val label: String, val icon: ImageVector) {
    WORKOUT("workout", "Workout", Icons.Filled.FitnessCenter),
    HISTORY("history", "History", Icons.Filled.History),
    PROGRAMS("programs", "Programs", Icons.Filled.List),
    EXERCISES("exercises", "Exercises", Icons.Filled.MenuBook),
    SETTINGS("settings", "Settings", Icons.Filled.Settings)
}
