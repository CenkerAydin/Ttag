package com.cenkeraydin.ttagmobil

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {

    NavHost(navController, startDestination = "splash") {
        composable("splash") {
            AnimatedSplashScreen(navController)
        }
        composable("home") {
            HomeScreen()
        }
    }
}