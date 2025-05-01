package com.cenkeraydin.ttagmobil.ui.logins

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cenkeraydin.ttagmobil.R
import com.cenkeraydin.ttagmobil.components.PasswordTextField
import com.cenkeraydin.ttagmobil.data.model.auth.LoginRequest
import com.cenkeraydin.ttagmobil.ui.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val backgroundImage = painterResource(id = R.drawable.beautiful_sunset)
    var selectedRole by remember { mutableStateOf("Passenger") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val viewModel: LoginViewModel = viewModel()
    val context = LocalContext.current
    val roleText = if (selectedRole == "Driver") stringResource(R.string.driver_login) else stringResource(R.string.passenger_login)
    val buttonColor = if (selectedRole == "Driver") Color(0xFFd32f2f) else Color(0xFF00796B)

    val profileViewModel: ProfileViewModel = viewModel()
    val loginState = viewModel.loginState

    LaunchedEffect(loginState) {
        loginState?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearLoginState() // Durumu sıfırla, yoksa tekrar gösterilir
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Arka Plan Resmi
        Image(
            painter = backgroundImage,
            contentDescription = "Background Car Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.welcome_back),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RoleSelectionButton(stringResource(R.string.passenger), selectedRole == "Passenger") {
                    selectedRole = "Passenger"
                }
                RoleSelectionButton(stringResource(R.string.driver), selectedRole == "Driver") {
                    selectedRole = "Driver"
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = roleText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = buttonColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email), color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White, fontWeight = FontWeight.Bold),
            )

            Spacer(modifier = Modifier.height(8.dp))

            PasswordTextField(password) {
                password = it
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val request = LoginRequest(email, password)
                    Log.e("SelectedRole", selectedRole)
                    viewModel.loginUser(
                        request,
                        navController,
                        context,
                        profileViewModel,
                        selectedRole
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.login), color = Color.White)
            }
            TextButton(onClick = {
                if (email.isNotEmpty()) {
                    viewModel.forgotPassword(email)
                } else {
                    Toast.makeText(
                        context,
                        "Lütfen e-posta adresinizi girin",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
                Text(stringResource(R.string.forgot_password), color = Color.White)
            }


            TextButton(onClick = { navController.navigate("register") }) {
                Text(stringResource(R.string.dont_have_account), color = Color.White)
            }

            if (viewModel.showResetDialog) {
                var newPassword by remember { mutableStateOf("") }
                var confirmPassword by remember { mutableStateOf("") }

                AlertDialog(
                    onDismissRequest = { viewModel.showResetDialog = false },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.resetPassword(
                                email = viewModel.resetEmail,
                                token = viewModel.resetToken,
                                password = newPassword,
                                confirmPassword = confirmPassword,
                                selectedRole = selectedRole

                            )
                            Log.e(
                                "ResetPassword",
                                "Email: ${viewModel.resetEmail}, Token: ${viewModel.resetToken}, Password: $newPassword"
                            )
                            viewModel.showResetDialog = false
                        }) {
                            Text(stringResource(R.string.reset_password))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.showResetDialog = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .background(Color(0xFF2C2C2C), shape = RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Resetting password for:",
                                color = Color.White
                            )
                            Text(
                                text = viewModel.resetEmail,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                label = { Text("New Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirm Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "Reset Password",
                            color = Color.White,
                            modifier = Modifier
                                .background(Color(0xFF2C2C2C))
                                .padding(8.dp)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    containerColor = Color(0xFF2C2C2C)
                )
            }


        }
    }
}

@Composable
fun RoleSelectionButton(role: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color.White else Color(0xFFE0E0E0),
            contentColor = if (isSelected) Color.Black else Color.Gray
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(4.dp)
    ) {
        Text(role, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}
