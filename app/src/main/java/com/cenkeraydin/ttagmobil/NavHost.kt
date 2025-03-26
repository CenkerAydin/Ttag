package com.cenkeraydin.ttagmobil

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cenkeraydin.ttagmobil.ui.logins.LoginScreen
import com.cenkeraydin.ttagmobil.ui.logins.RegisterScreen
import com.cenkeraydin.ttagmobil.ui.splash.AnimatedSplashScreen


@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {

    NavHost(navController, startDestination = "splash") {
        composable("splash") {
            AnimatedSplashScreen(navController)
        }
        composable("login") {
            LoginScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
    }
}