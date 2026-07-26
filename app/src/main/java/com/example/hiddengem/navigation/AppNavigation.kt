package com.example.hiddengem.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hiddengem.auth.LoginScreen
import com.example.hiddengem.map.MapScreen
import com.example.hiddengem.addspot.AddSpotScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login")   { LoginScreen(navController) }
        composable("map")     { MapScreen(navController) }
        composable("addSpot") { AddSpotScreen(navController) }
    }
}