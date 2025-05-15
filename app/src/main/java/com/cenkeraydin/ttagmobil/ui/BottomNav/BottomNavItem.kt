package com.cenkeraydin.ttagmobil.ui.BottomNav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Person4
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    data object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    data object Reservation : BottomNavItem("reservation", "Reservation", Icons.Default.DateRange)
    data object Cars : BottomNavItem("cars", "Cars", Icons.Default.DirectionsCar)
    data object Drivers: BottomNavItem("drivers", "Drivers", Icons.Default.Person4)
    data object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}
