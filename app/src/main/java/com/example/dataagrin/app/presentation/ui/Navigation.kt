package com.example.dataagrin.app.presentation.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dataagrin.app.ui.theme.AppTheme

sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    object Tasks : Screen("tasks", "Tarefas", Icons.Default.CheckCircle)

    object TaskRegistry : Screen("taskregistry", "Registrar", Icons.Default.AddCircle)

    object Weather : Screen("weather", "Clima", Icons.Default.CloudQueue)
}

val items =
    listOf(
        Screen.Tasks,
        Screen.TaskRegistry,
        Screen.Weather,
    )

@Composable
fun AppNavigation(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()
    val isExpandedScreen = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    val colors = AppTheme.colors
    val isDark = isSystemInDarkTheme()

    // Cor verde do app
    val highlightGreen = colors.primary

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = colors.surface,
            ) {
                val navBackStackEntry = navController.currentBackStackEntryAsState().value
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    NavigationBarItem(
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = null,
                                tint =
                                    if (isSelected) {
                                        if (isDark) {
                                            Color.White
                                        } else {
                                            highlightGreen
                                        }
                                    } else if (isDark) {
                                        highlightGreen
                                    } else {
                                        colors.textTertiary
                                    },
                            )
                        },
                        label = {
                            Text(
                                screen.label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color =
                                    if (isSelected) {
                                        if (isDark) {
                                            Color.White
                                        } else {
                                            highlightGreen
                                        }
                                    } else if (isDark) {
                                        highlightGreen
                                    } else {
                                        colors.textTertiary
                                    },
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors =
                            NavigationBarItemDefaults.colors(
                                selectedIconColor = if (isDark) Color.White else highlightGreen,
                                selectedTextColor = if (isDark) Color.White else highlightGreen,
                                indicatorColor = if (isDark) highlightGreen else colors.primarySurface,
                            ),
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Tasks.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Tasks.route) {
                TaskScreen(
                    isExpandedScreen = isExpandedScreen,
                    onNavigateToRegister = {
                        navController.navigate(Screen.TaskRegistry.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
            composable(Screen.TaskRegistry.route) { TaskRegistryScreen(isExpandedScreen = isExpandedScreen) }
            composable(Screen.Weather.route) { WeatherScreen(isExpandedScreen = isExpandedScreen) }
        }
    }
}
