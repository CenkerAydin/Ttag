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
import androidx.compose.material3.TextFieldDefaults
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
import com.cenkeraydin.ttagmobil.data.model.LoginRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val backgroundImage = painterResource(id = R.drawable.beautiful_sunset)
    var selectedRole by remember { mutableStateOf("passenger") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val viewModel: LoginViewModel = viewModel()
    val context = LocalContext.current
    val roleText = if (selectedRole == "driver") "Driver Login" else "Passenger Login"
    val buttonColor = if (selectedRole == "driver") Color(0xFFd32f2f) else Color(0xFF00796B)

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
                text = "Welcome Again!",
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
                RoleSelectionButton("Passenger", selectedRole == "passenger") {
                    selectedRole = "passenger"
                }
                RoleSelectionButton("Driver", selectedRole == "driver") {
                    selectedRole = "driver"
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
                label = { Text("Email", color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White, fontWeight = FontWeight.Bold),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color.Blue,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            PasswordTextField(password) {
                password = it
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val request = LoginRequest(email, password)
                    viewModel.loginUser(request, navController,context)
                },
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login", color = Color.White)
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
                Text("Forgot Password?", color = Color.White)
            }


            TextButton(onClick = { navController.navigate("register") }) {
                Text("Don't have an account? Sign Up", color = Color.White)
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
                                confirmPassword = confirmPassword

                            )
                            Log.e("ResetPassword", "Email: ${viewModel.resetEmail}, Token: ${viewModel.resetToken}, Password: $newPassword")
                            viewModel.showResetDialog = false
                        }) {
                            Text("Reset Password")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.showResetDialog = false }) {
                            Text("Cancel")
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
