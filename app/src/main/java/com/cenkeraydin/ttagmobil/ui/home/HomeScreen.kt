package com.cenkeraydin.ttagmobil.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.cenkeraydin.ttagmobil.ui.profile.ProfileViewModel
import com.cenkeraydin.ttagmobil.ui.reservation.ReservationViewModel


@Composable
fun HomeScreen(navHostController: NavHostController, viewModel: ReservationViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Ttag")
                },
                backgroundColor = Color.Black,
                contentColor = Color.White
            )
        }
    ) { padding ->

        val profileViewModel: ProfileViewModel = viewModel()
        val selectedRole = profileViewModel.selectedRole.collectAsState().value
        when (selectedRole) {
            "Passenger" -> {
                UserHomeScreen(viewModel,modifier = Modifier.padding(padding))
            }

            "Driver" -> {
                DriverHomeScreen(viewModel,modifier = Modifier.padding(padding))
            }

            else -> {
                Text(text = "Role not selected")
            }
        }

        }

}