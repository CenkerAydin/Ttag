package com.cenkeraydin.ttagmobil.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cenkeraydin.ttagmobil.components.DeleteAccountButton
import com.cenkeraydin.ttagmobil.components.LogoutButton
import com.cenkeraydin.ttagmobil.ui.logins.LoginViewModel

@Composable
fun ProfileScreen(navHostController: NavHostController) {
    val context = LocalContext.current

    val profileViewModel: ProfileViewModel = viewModel()
    val user = profileViewModel.user.collectAsState().value
    val driver = profileViewModel.driver.collectAsState().value
    val selectedRole = profileViewModel.selectedRole.collectAsState().value


    val loginViewModel: LoginViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        when (selectedRole) {
            "Passenger" -> {
                PassengerProfileScreen(user)
            }

            "Driver" -> {
                DriverProfileScreen(driver)
            }

            else -> {
                Text(text = "Role not selected")
            }
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            LogoutButton {
                loginViewModel.logoutUser(context, navHostController, profileViewModel)
            }
            DeleteAccountButton {
                profileViewModel.deleteAccount(
                    context,
                    navHostController,
                    selectedRole = selectedRole
                )
            }
        }
    }


}

@Preview
@Composable
fun ProfileScreenPreview() {
    val navHostController = rememberNavController()
    ProfileScreen(navHostController)
}

