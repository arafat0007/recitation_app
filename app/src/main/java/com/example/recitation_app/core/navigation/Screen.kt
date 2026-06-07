package com.example.recitation_app.core.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object SurahList : Screen("surah_list")
    object SurahDetail : Screen("surah_detail/{surahId}") {
        fun createRoute(surahId: String) = "surah_detail/$surahId"
    }
    object DoaList : Screen("doa_list")
    object DoaDetail : Screen("doa_detail/{doaId}") {
        fun createRoute(doaId: String) = "doa_detail/$doaId"
    }
    object OwajList : Screen("owaj_list")
    object OwajDetail : Screen("owaj_detail/{owajId}") {
        fun createRoute(owajId: String) = "owaj_detail/$owajId"
    }
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")
    object Login : Screen("login")
    object Register : Screen("register")
}
