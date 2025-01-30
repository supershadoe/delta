package dev.shadoe.delta.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.shadoe.delta.screens.HomeScreen
import dev.shadoe.delta.screens.ShizukuSetup
import dev.shadoe.delta.shizuku.LocalShizukuConnected
import dev.shadoe.delta.shizuku.LocalShizukuRunning

val LocalNavController = staticCompositionLocalOf<NavHostController?> { null }

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(
            navController = LocalNavController.current!!,
            startDestination = if (LocalShizukuConnected.current && LocalShizukuRunning.current) {
                Routes.HomeScreen
            } else {
                Routes.ShizukuSetup
            }
        ) {
            composable<Routes.ShizukuSetup> { ShizukuSetup() }
            composable<Routes.HomeScreen> { HomeScreen() }
        }
    }
}