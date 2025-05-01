package com.cenkeraydin.ttagmobil.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cenkeraydin.ttagmobil.R
import com.cenkeraydin.ttagmobil.data.model.account.UpdateDriverInfoRequest
import com.cenkeraydin.ttagmobil.ui.profile.ProfileViewModel

@Composable
fun UpdateDriverDialog(
    initialEmail: String?,
    initialLicenseUrl: String,
    onDismiss: () -> Unit,
    onConfirm: (UpdateDriverInfoRequest) -> Unit
) {
    var email by remember { mutableStateOf(initialEmail ?: "") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var experienceYear by remember { mutableStateOf("") }
    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var experienceYearError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.updateInfo)) },
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
                    label = { Text(stringResource(R.string.email)) },
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
                    label = { Text(stringResource(R.string.name)) },
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
                    label = { Text(stringResource(R.string.surname)) },
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
                            contentDescription = "Phone ikonu"
                        )
                    },
                    label = { Text(stringResource(R.string.phone)) },
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
                    label = { Text(stringResource(R.string.password)) },
                    isError = passwordError != null,
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                if (passwordError != null) {
                    Text(passwordError!!, color = Color.Red, fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = experienceYear,
                    onValueChange = {
                        experienceYear = it
                        experienceYearError = null
                    },
                    leadingIcon ={
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Experience ikonu"
                        )
                    },
                    label = { Text(stringResource(R.string.experience_years)) },  // Driver-specific field
                    isError = experienceYearError != null,
                    singleLine = true
                )
                if (experienceYearError != null) {
                    Text(experienceYearError!!, color = Color.Red, fontSize = 12.sp)
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
                } else if (!isPasswordValid(password)) {
                    passwordError = "Şifre en az bir büyük harf, bir küçük harf, bir rakam ve bir özel karakter içermelidir."
                    hasError = true
                }
                if (phone.isBlank()) {
                    phoneError = "Telefon boş olamaz"
                    hasError = true
                } else if (!phone.matches(Regex("^\\d{10,11}\$"))) {
                    phoneError = "Geçerli bir telefon numarası girin"
                    hasError = true
                }

                if (experienceYear.isBlank()) {
                    hasError = true
                    experienceYearError = "Bu alan boş olamaz"
                } else {
                    try {
                        val experience = experienceYear.toInt()
                        if (experience < 0 || experience > 50) {
                            // Add appropriate error handling
                            hasError = true
                            experienceYearError = "Deneyim yılı 0 ile 50 arasında olmalıdır"
                        }
                    } catch (e: NumberFormatException) {
                        // Add appropriate error handling
                        hasError = true
                    }
                }

                if (!hasError) {
                    val request = UpdateDriverInfoRequest(
                        email = initialEmail,
                        firstName = firstName,
                        lastName = lastName,
                        phoneNumber = phone,
                        password = password,
                        licenseUrl = initialLicenseUrl, // driver'dan çek
                        experienceYear = experienceYear.toInt() // Driver-specific field
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
