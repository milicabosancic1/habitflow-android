package com.habitflow.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Today
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.habitflow.app.ui.auth.LoginScreen
import com.habitflow.app.ui.auth.RegisterScreen
import com.habitflow.app.ui.calendar.CalendarScreen
import com.habitflow.app.ui.habit.CreateHabitScreen
import com.habitflow.app.ui.habit.HabitDetailScreen
import com.habitflow.app.ui.home.HomeScreen
import com.habitflow.app.ui.onboarding.OnboardingScreen
import com.habitflow.app.ui.profile.ProfileScreen
import com.habitflow.app.ui.stats.RecommendationsScreen
import com.habitflow.app.ui.stats.StatsScreen

sealed class Dest(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Dest("home", "Danas", Icons.Rounded.Today)
    data object Stats : Dest("stats", "Statistika", Icons.Rounded.BarChart)
    data object Calendar : Dest("calendar", "Kalendar", Icons.Rounded.CalendarMonth)
    data object Recs : Dest("recs", "Preporuke", Icons.Rounded.Lightbulb)
    data object Profile : Dest("profile", "Profil", Icons.Rounded.Person)
}

private val bottomItems = listOf(Dest.Home, Dest.Stats, Dest.Calendar, Dest.Recs, Dest.Profile)

@Composable
fun HabitFlowAppRoot(gateViewModel: AppGateViewModel = hiltViewModel()) {
    val hasOnboarded by gateViewModel.hasOnboarded.collectAsStateWithLifecycle()

    if (!hasOnboarded) {
        OnboardingScreen()
        return
    }

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomItems.forEach { dest ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true,
                        onClick = {
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(dest.icon, contentDescription = dest.label) },
                        label = { Text(dest.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Dest.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Dest.Home.route) {
                HomeScreen(
                    onAddHabit = { navController.navigate("create") },
                    onOpenHabit = { id -> navController.navigate("detail/$id") }
                )
            }
            composable(Dest.Stats.route) { StatsScreen() }
            composable(Dest.Calendar.route) { CalendarScreen() }
            composable(Dest.Recs.route) { RecommendationsScreen() }
            composable(Dest.Profile.route) {
                ProfileScreen(
                    onLogin = { navController.navigate("login") },
                    onRegister = { navController.navigate("register") }
                )
            }
            composable("login") {
                LoginScreen(
                    onBack = { navController.popBackStack() },
                    onLoggedIn = { navController.popBackStack() }
                )
            }
            composable("register") {
                RegisterScreen(
                    onBack = { navController.popBackStack() },
                    onRegistered = { navController.popBackStack() }
                )
            }
            composable("create") {
                CreateHabitScreen(onDone = { navController.popBackStack() })
            }
            composable("detail/{habitId}") { entry ->
                HabitDetailScreen(
                    habitId = entry.arguments?.getString("habitId") ?: "",
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
