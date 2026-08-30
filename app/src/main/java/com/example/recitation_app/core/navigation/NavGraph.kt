package com.example.recitation_app.core.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.recitation_app.feature_auth.ui.LoginScreen
import com.example.recitation_app.feature_auth.ui.RegisterScreen
import com.example.recitation_app.feature_doa.ui.AdhkarDetailScreen
import com.example.recitation_app.feature_doa.ui.DoaCategoryScreen
import com.example.recitation_app.feature_doa.ui.DoaViewModel
import com.example.recitation_app.feature_doa.ui.PostSalahAdhkarScreen
import com.example.recitation_app.feature_home.ui.HomeScreen
import com.example.recitation_app.feature_owaj.ui.OwajDetailScreen
import com.example.recitation_app.feature_owaj.ui.OwajListScreen
import com.example.recitation_app.feature_zikir.ui.ZikirListScreen
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
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
        
        composable(Screen.DoaList.route) {
            DoaCategoryScreen(
                onBackClick = { navController.popBackStack() },
                onPostSalahAdhkarClick = { navController.navigate(Screen.PostSalahAdhkar.route) },
                onZikirClick = { navController.navigate(Screen.ZikirList.route) }
            )
        }

        composable(Screen.PostSalahAdhkar.route) {
            val viewModel: DoaViewModel = viewModel()
            PostSalahAdhkarScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onAdhkarClick = { id, salahKey ->
                    navController.navigate(Screen.AdhkarDetail.createRoute(id, salahKey))
                }
            )
        }

        composable(
            route = Screen.AdhkarDetail.route,
            arguments = listOf(
                navArgument("adhkarId") { type = NavType.StringType },
                navArgument("salahKey") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val adhkarId = backStackEntry.arguments?.getString("adhkarId") ?: ""
            val salahKey = backStackEntry.arguments?.getString("salahKey") ?: ""
            
            // Try to get the ViewModel scoped to PostSalahAdhkar route to preserve session state
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.PostSalahAdhkar.route)
            }
            val viewModel: DoaViewModel = viewModel(parentEntry)

            AdhkarDetailScreen(
                adhkarId = adhkarId,
                salahKey = salahKey,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        
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
        
        composable(Screen.ZikirList.route) {
            ZikirListScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
