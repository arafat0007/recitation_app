package com.example.recitation_app.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.recitation_app.feature_home.ui.HomeScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onSurahClick = { navController.navigate(Screen.SurahList.route) },
                onDoaClick = { navController.navigate(Screen.DoaList.route) },
                onOwajClick = { navController.navigate(Screen.OwajList.route) },
                onFavoritesClick = { navController.navigate(Screen.Favorites.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }
        
        composable(Screen.SurahList.route) { /* TODO */ }
        composable(Screen.DoaList.route) { /* TODO */ }
        composable(Screen.OwajList.route) { /* TODO */ }
        composable(Screen.Favorites.route) { /* TODO */ }
        composable(Screen.Settings.route) { /* TODO */ }
    }
}
