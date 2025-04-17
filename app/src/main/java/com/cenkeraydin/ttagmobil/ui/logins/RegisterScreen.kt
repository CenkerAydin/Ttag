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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cenkeraydin.ttagmobil.R
import com.cenkeraydin.ttagmobil.components.PasswordTextField
import com.cenkeraydin.ttagmobil.data.model.RegisterRequest
import com.cenkeraydin.ttagmobil.ui.EmailConfirmDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val backgroundImage = painterResource(id = R.drawable.register_background)
    var selectedRole by remember { mutableStateOf("Driver") }
    var name by remember { mutableStateOf("") }
    var surName by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val viewModel: RegisterViewModel = viewModel()
    var showConfirmationDialog by remember { mutableStateOf(false) }

    val roleText = if (selectedRole == "Driver") "Driver Register" else "Passenger Register"
    val buttonColor = if (selectedRole == "Basic") Color.Red else Color(0xFF00796B)

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
                text = "Welcome to Ttag!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RoleSelectionButton("Basic", selectedRole == "Basic") {
                    selectedRole = "Basic"
                }
                RoleSelectionButton("Driver", selectedRole == "Driver") {
                    selectedRole = "Driver"
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = roleText,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = buttonColor
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name", color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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

            OutlinedTextField(
                value = surName,
                onValueChange = { surName = it },
                label = { Text("Surname", color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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

            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Username", color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone", color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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
                    val request = RegisterRequest(
                        firstName = name,
                        lastName = surName,
                        email = email,
                        userName = userName,
                        phoneNumber = phoneNumber,
                        password = password,
                        confirmPassword = password
                    )
                    viewModel.registerUser(
                        request,
                        onSuccess = {
                            showConfirmationDialog = true // Kayıt başarılıysa popup açılsın
                        },
                        selectedRole = selectedRole,
                        onError = {
                            Log.e("RegisterError", "Kayıt başarısız: $it")
                            Toast.makeText(context, "Kayıt başarısız: $it", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register", color = Color.White)
            }

            TextButton(onClick = { navController.navigate("login") }) {
                Text("Already have an account? Sign In", color = Color.White)
            }

            viewModel.registrationState?.let { state ->
                Text(
                    text = when {
                        state == "success" -> "Kayıt başarılı!"
                        state?.startsWith("error") == true -> "Kayıt başarısız: $state"
                        state?.startsWith("failure") == true -> "Sunucuya bağlanılamadı: $state"
                        else -> ""
                    },
                    color = if (state == "success") Color.Green else Color.Red
                )
                Log.e("RegisterError", state)

            }

            if (showConfirmationDialog) {
                EmailConfirmDialog(
                    email = email,
                    onDismiss = { showConfirmationDialog = false },
                    onSuccess = {
                        showConfirmationDialog = false
                        navController.navigate("home")
                    }
                )
            }
        }

    }
}

@Composable
@Preview(showBackground = true)
fun RegisterScreenPreview() {
    RegisterScreen(navController = rememberNavController())
}