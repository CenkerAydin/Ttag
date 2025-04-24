package com.cenkeraydin.ttagmobil

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cenkeraydin.ttagmobil.ui.BottomNav.BottomNavigationBar
import com.cenkeraydin.ttagmobil.ui.home.CarScreen
import com.cenkeraydin.ttagmobil.ui.home.HomeScreen
import com.cenkeraydin.ttagmobil.ui.home.ReservationScreen
import com.cenkeraydin.ttagmobil.ui.logins.LoginScreen
import com.cenkeraydin.ttagmobil.ui.logins.LoginViewModel
import com.cenkeraydin.ttagmobil.ui.logins.RegisterScreen
import com.cenkeraydin.ttagmobil.ui.profile.ProfileScreen
import com.cenkeraydin.ttagmobil.ui.splash.AnimatedSplashScreen


@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    val bottomBarRoutes = listOf(
        "home", "reservation", "cars", "profile"
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") { AnimatedSplashScreen(navController) }
            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }

            // Bottom bar ekranları
            composable("home") { HomeScreen(navController) }
            composable("reservation") { ReservationScreen(navController) }
            composable("cars") { CarScreen(navController) }
            composable("profile") { ProfileScreen(navController) }
//               }


        }
    }
}
