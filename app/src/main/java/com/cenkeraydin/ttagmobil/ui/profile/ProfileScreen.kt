package com.cenkeraydin.ttagmobil.ui.profile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.cenkeraydin.ttagmobil.R
import com.cenkeraydin.ttagmobil.data.model.UpdateUserInfoRequest
import com.cenkeraydin.ttagmobil.ui.logins.LoginViewModel
import com.cenkeraydin.ttagmobil.util.UserPrefsHelper

@Composable
fun ProfileScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val profileViewModel : ProfileViewModel = viewModel()
    val user = profileViewModel.user.collectAsState().value
    var showDialog by remember { mutableStateOf(false) }

    val loginViewModel :LoginViewModel = viewModel()
    var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(Unit) {
        profileBitmap = UserPrefsHelper(context).getProfileImage()
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }

                profileBitmap = bitmap
                val base64 = UserPrefsHelper(context).encodeBitmapToBase64(bitmap)
                UserPrefsHelper(context).saveProfileImage(base64)
            }
        }
    )


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Profile")
                },
                backgroundColor = Color.Black,
                contentColor = Color.White
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .clickable {
                        imagePickerLauncher.launch("image/*")
                    }
            ) {
                if (profileBitmap != null) {
                    Image(
                        bitmap = profileBitmap!!.asImageBitmap(),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Default Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

            user?.let {
                it.firstName?.let { it1 -> ReadOnlyTextField(label = "Name", value = it1) }
                it.lastName?.let { it1 -> ReadOnlyTextField(label = "Surname", value = it1) }
                it.phoneNumber?.let { it1 -> ReadOnlyTextField(label = "Phone", value = it1) }
                it.email?.let { it1 -> ReadOnlyTextField(label = "Email", value = it1) }
                it.userName?.let { it1 -> ReadOnlyTextField(label = "Username", value = it1) }
            } ?: CircularProgressIndicator()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // butonlar arası boşluk
            ) {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EE)) // örnek renk
                ) {
                    Text("Bilgileri Güncelle", color = Color.White)
                }

                Button(
                    onClick = {
                        loginViewModel.logoutUser(context, navHostController, profileViewModel)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EE))
                ) {
                    Text("Çıkış Yap", color = Color.White)
                }

                Button(
                    onClick = {
                        profileViewModel.deleteAccount(context, navHostController)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD32F2F)) // kırmızımsı
                ) {
                    Text("Hesabı Sil", color = Color.White)
                }
            }


            if (showDialog && user != null) {
                UpdateUserDialog(
                    initialEmail = user.email,
                    onDismiss = { showDialog = false },
                    onConfirm = { request ->
                        showDialog = false
                        profileViewModel.updateUserInfo(request, context)
                    }
                )
            }


        }
    }
}


@Composable
fun ReadOnlyTextField(label: String, value: String, isPassword: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { androidx.compose.material3.Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        readOnly = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true
    )
}

@Composable
fun UpdateUserDialog(
    initialEmail: String?,
    onDismiss: () -> Unit,
    onConfirm: (UpdateUserInfoRequest) -> Unit
) {
    var email by remember { mutableStateOf(initialEmail ?: "") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bilgileri Güncelle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-posta") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = firstName,
                    onValueChange = {
                        firstName = it
                        firstNameError = null
                    },
                    label = { Text("Ad") },
                    isError = firstNameError != null,
                    singleLine = true
                )
                if (firstNameError != null) {
                    Text(firstNameError!!, color = Color.Red, fontSize = 12.sp)
                }
                OutlinedTextField(
                    value = lastName,
                    onValueChange = {
                        lastName = it
                        lastNameError = null
                    },
                    label = { Text("Soyad") },
                    isError = lastNameError != null,
                    singleLine = true
                )
                if (lastNameError != null) {
                    Text(lastNameError!!, color = Color.Red, fontSize = 12.sp)
                }
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        phoneError = null
                    },
                    label = { Text("Telefon") },
                    isError = phoneError != null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                if (phoneError != null) {
                    Text(phoneError!!, color = Color.Red, fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                    },
                    label = { Text("Şifre") },
                    isError = passwordError != null,
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                if (passwordError != null) {
                    Text(passwordError!!, color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                var hasError = false

                if (firstName.isBlank()) {
                    firstNameError = "Bu alan boş olamaz"
                    hasError = true
                }
                if (lastName.isBlank()) {
                    lastNameError = "Bu alan boş olamaz"
                    hasError = true
                }
                if (password.isBlank()) {
                    passwordError = "Şifre gerekli"
                    hasError = true
                }
                if (phone.isBlank()) {
                    phoneError = "Telefon boş olamaz"
                    hasError = true
                } else if (!phone.matches(Regex("^\\d{10,11}\$"))) {
                    phoneError = "Geçerli bir telefon numarası girin"
                    hasError = true
                }

                if (!hasError) {
                    val request = UpdateUserInfoRequest(
                        email = initialEmail,
                        firstName = firstName,
                        lastName = lastName,
                        phoneNumber = phone,
                        password = password,
                    )
                    onConfirm(request)
                }
            }) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

