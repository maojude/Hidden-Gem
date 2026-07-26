package com.example.hiddengem.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.example.hiddengem.auth.LoginScreen
import com.example.hiddengem.map.MapScreen
import com.example.hiddengem.map.PickLocationScreen
import com.example.hiddengem.addspot.AddSpotScreen
import com.example.hiddengem.detail.SpotDetailScreen
import com.example.hiddengem.profile.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val start = if (FirebaseAuth.getInstance().currentUser != null) "map" else "login"

    NavHost(navController = navController, startDestination = start) {
        composable("login")        { LoginScreen(navController) }
        composable("map")          { MapScreen(navController) }
        composable("pickLocation") { PickLocationScreen(navController) }
        composable("addSpot")      { AddSpotScreen(navController) }
        composable("profile")      { ProfileScreen(navController) }
        composable("spotDetail/{spotId}") { entry ->
            SpotDetailScreen(navController, entry.arguments?.getString("spotId") ?: "")
        }
    }
}