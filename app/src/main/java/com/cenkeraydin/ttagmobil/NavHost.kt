package com.cenkeraydin.ttagmobil

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cenkeraydin.ttagmobil.ui.BottomNav.BottomNavigationBar
import com.cenkeraydin.ttagmobil.ui.car.CarListScreen
import com.cenkeraydin.ttagmobil.ui.home.HomeScreen
import com.cenkeraydin.ttagmobil.ui.logins.LoginScreen
import com.cenkeraydin.ttagmobil.ui.logins.RegisterScreen
import com.cenkeraydin.ttagmobil.ui.profile.ProfileScreen
import com.cenkeraydin.ttagmobil.ui.reservation.MakeReservationScreen
import com.cenkeraydin.ttagmobil.ui.reservation.ReservationViewModel
import com.cenkeraydin.ttagmobil.ui.reservation.UserReservationScreen
import com.cenkeraydin.ttagmobil.ui.splash.AnimatedSplashScreen
import com.cenkeraydin.ttagmobil.util.PreferencesHelper


@Composable
fun AppNavHost(navController: NavHostController = rememberNavController(),context: Context) {

    val preferencesHelper = PreferencesHelper(context)
    val userRole = preferencesHelper.getSelectedRole() ?: "Passenger" // Varsayılan olarak 'passenger'
    val bottomBarRoutes = listOf(
        "home", "reservation", "cars", "profile"
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val reservationViewModel: ReservationViewModel = viewModel()

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                BottomNavigationBar(navController,userRole)
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
            composable("home") { HomeScreen(navController, reservationViewModel) }
            composable("reservation") { UserReservationScreen(navController, reservationViewModel) }
            composable("cars") { CarListScreen(navController) }
            composable("profile") { ProfileScreen(navController) }
            composable(
                route = "makeReservation/{startDate}/{startHour}/{endDate}/{endHour}/{fromWhere}/{toWhere}/{km}",
                arguments = listOf(
                    navArgument("startDate") { type = NavType.StringType },
                    navArgument("startHour") { type = NavType.StringType },
                    navArgument("endDate") { type = NavType.StringType },
                    navArgument("endHour") { type = NavType.StringType },
                    navArgument("fromWhere") { type = NavType.StringType },
                    navArgument("toWhere") { type = NavType.StringType },
                    navArgument("km") { type = NavType.IntType }

                )
            ) {
                val startDate = it.arguments?.getString("startDate") ?: ""
                val startHour = it.arguments?.getString("startHour") ?: ""
                val endDate = it.arguments?.getString("endDate") ?: ""
                val endHour = it.arguments?.getString("endHour") ?: ""
                val fromWhere = it.arguments?.getString("fromWhere") ?: ""
                val toWhere = it.arguments?.getString("toWhere") ?: ""
                val km = it.arguments?.getInt("km") ?: 0

                MakeReservationScreen(
                    startDate = startDate,
                    startHour = startHour,
                    endDate= endDate,
                    endHour = endHour,
                    fromWhere = fromWhere,
                    toWhere = toWhere,
                    km = km,
                    viewModel = reservationViewModel,
                    navHostController = navController,

                )
            }




        }
    }
}
