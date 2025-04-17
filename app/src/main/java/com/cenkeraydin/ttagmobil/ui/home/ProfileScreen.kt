package com.cenkeraydin.ttagmobil.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.cenkeraydin.ttagmobil.ui.logins.LoginViewModel

@Composable
fun ProfileScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val viewModel :LoginViewModel = viewModel()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                viewModel.logoutUser(context, navHostController)
            },
        ) {
            Text("Çıkış Yap", color = Color.White)
        }
    }
}
