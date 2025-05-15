package com.cenkeraydin.ttagmobil.ui.BottomNav

import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavHostController, userRole: String?) {

    val items = when (userRole) {
        "Driver" -> listOf(
            BottomNavItem.Home,
            BottomNavItem.Cars,
            BottomNavItem.Profile
        )
        else -> listOf(
            BottomNavItem.Home,
            BottomNavItem.Reservation,
            BottomNavItem.Cars,
            BottomNavItem.Drivers,
            BottomNavItem.Profile
        )
    }

    BottomNavigation(
        modifier = Modifier.height(72.dp),
        backgroundColor = Color.Black,
        contentColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = {  Text(
                    item.label,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    fontSize = 10.sp // bunu istersen ekleyebilirsin
                ) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                selectedContentColor = Color.Yellow,
                unselectedContentColor = Color.Gray
            )
        }
    }
}
