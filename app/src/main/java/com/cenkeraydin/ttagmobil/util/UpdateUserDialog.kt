package com.cenkeraydin.ttagmobil.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cenkeraydin.ttagmobil.R
import com.cenkeraydin.ttagmobil.data.model.account.UpdateUserInfoRequest

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
        title = { Text(stringResource(R.string.updateInfo), color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "E-posta ikonu"
                        )
                    },
                    label = { Text(stringResource(R.string.email), color = MaterialTheme.colorScheme.onSurface) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = firstName,
                    onValueChange = {
                        firstName = it
                        firstNameError = null
                    },
                    leadingIcon ={
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Ad ikonu"
                        )
                    },
                    label = { Text(stringResource(R.string.name), color = MaterialTheme.colorScheme.onSurface) },
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
                    leadingIcon ={
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Surname ikonu"
                        )
                    },
                    label = { Text(stringResource(R.string.surname),color = MaterialTheme.colorScheme.onSurface) },
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
                    leadingIcon ={
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Ad ikonu"
                        )
                    },
                    label = { Text(stringResource(R.string.phone),color = MaterialTheme.colorScheme.onSurface) },
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
                    leadingIcon ={
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password ikonu"
                        )
                    },
                    label = { Text(stringResource(R.string.password),color = MaterialTheme.colorScheme.onSurface) },
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
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}