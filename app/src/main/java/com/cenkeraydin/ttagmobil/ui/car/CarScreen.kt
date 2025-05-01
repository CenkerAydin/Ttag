package com.cenkeraydin.ttagmobil.ui.car

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun CarListScreen(navHostController: NavHostController) {
    val viewModel: CarViewModel = viewModel()
    val selectedRole = viewModel.selectedRole.collectAsState().value


    Column(modifier = Modifier.fillMaxSize()) {
        when (selectedRole) {
            "Passenger" -> {
                UserCarScreen(navHostController)
            }

            "Driver" -> {
                DriverCarScreen(navHostController)
            }

            else -> {
                androidx.compose.material.Text(text = "Role not selected")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CarListScreenPreview() {
    val navHostController = NavHostController(LocalContext.current)
    CarListScreen(navHostController)
}