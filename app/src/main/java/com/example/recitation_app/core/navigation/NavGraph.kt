package com.example.recitation_app.core.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.recitation_app.feature_auth.ui.LoginScreen
import com.example.recitation_app.feature_auth.ui.RegisterScreen
import com.example.recitation_app.feature_home.ui.HomeScreen
import com.example.recitation_app.feature_owaj.ui.OwajDetailScreen
import com.example.recitation_app.feature_owaj.ui.OwajListScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            Log.d("FLOW", "NavGraph: Navigating to Home Screen")
            HomeScreen(
                onSurahClick = { navController.navigate(Screen.SurahList.route) },
                onDoaClick = { navController.navigate(Screen.DoaList.route) },
                onOwajClick = { 
                    Log.d("FLOW", "NavGraph: Home -> OwajList")
                    navController.navigate(Screen.OwajList.route) 
                },
                onFavoritesClick = { navController.navigate(Screen.Favorites.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }
        
        composable(Screen.SurahList.route) { /* TODO */ }
        composable(Screen.DoaList.route) { /* TODO */ }
        
        composable(Screen.OwajList.route) {
            Log.d("FLOW", "NavGraph: Rendering OwajListScreen")
            OwajListScreen(
                onBackClick = {
                    Log.d("FLOW", "NavGraph: OwajList -> Home")
                    navController.popBackStack()
                },
                onOwajClick = { id ->
                    Log.d("FLOW", "NavGraph: OwajList -> OwajDetail (id: $id)")
                    navController.navigate(Screen.OwajDetail.createRoute(id))
                }
            )
        }
        
        composable(
            route = Screen.OwajDetail.route,
            arguments = listOf(navArgument("owajId") { type = NavType.StringType })
        ) { backStackEntry ->
            val owajId = backStackEntry.arguments?.getString("owajId") ?: ""
            Log.d("FLOW", "NavGraph: Rendering OwajDetailScreen (id: $owajId)")
            OwajDetailScreen(
                owajId = owajId,
                onBackClick = { 
                    Log.d("FLOW", "NavGraph: OwajDetail -> Back")
                    navController.popBackStack() 
                }
            )
        }

        composable(Screen.Favorites.route) { /* TODO */ }
        composable(Screen.Settings.route) { /* TODO */ }
    }
}
