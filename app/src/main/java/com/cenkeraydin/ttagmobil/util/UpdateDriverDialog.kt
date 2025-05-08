package com.cenkeraydin.ttagmobil.util

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.cenkeraydin.ttagmobil.R
import com.cenkeraydin.ttagmobil.data.model.account.UpdateDriverInfoRequest
import com.cenkeraydin.ttagmobil.ui.profile.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun UpdateDriverDialog(
    initialEmail: String,
    initialPassword: String,
    initialLicenseUrl: String,
    onConfirm: (UpdateDriverInfoRequest) -> Unit,
    onDismiss: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf(initialEmail) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf(initialPassword) }
    var experienceYear by remember { mutableStateOf("") }
    var licenseUrl by remember { mutableStateOf(initialLicenseUrl) }
    var licenseBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var experienceYearError by remember { mutableStateOf<String?>(null) }

    val licenseImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                Log.d("LicenseUpload", "Seçilen URI: $uri, Şema: ${uri.scheme}")
                if (uri.scheme != "content") {
                    Log.e("LicenseUpload", "Geçersiz URI şeması: ${uri.scheme}")
                    errorMessage = "Geçersiz görsel formatı"
                    return@let
                }

                val bitmap = try {
                    if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                    } else {
                        val source = ImageDecoder.createSource(context.contentResolver, it)
                        ImageDecoder.decodeBitmap(source)
                    }
                } catch (e: Exception) {
                    Log.e("LicenseUpload", "Bitmap oluşturma hatası: ${e.message}")
                    errorMessage = "Görsel yüklenemedi: ${e.message}"
                    return@let
                }

                Log.d("LicenseUpload", "Bitmap: ${bitmap.width}x${bitmap.height}, isRecycled: ${bitmap.isRecycled}")
                licenseBitmap = bitmap
                isLoading = true

                val userId = UserPrefsHelper(context).getUserId()
                if (userId.isNullOrBlank()) {
                    Log.e("LicenseUpload", "userId boş veya null")
                    errorMessage = "Kullanıcı kimliği bulunamadı"
                    isLoading = false
                    return@let
                }

                // Görseli yükle
                CoroutineScope(Dispatchers.IO).launch {
                    profileViewModel.uploadDriverLicense(
                        bitmap = bitmap,
                        context = context,
                        userId = userId,
                        onSuccess = { uploadedUrl ->
                            licenseUrl = uploadedUrl // API'den dönen URL'yi licenseUrl'ye ata
                            isLoading = false
                            errorMessage = null
                            Log.d("LicenseUpload", "Lisans görseli yüklendi: $uploadedUrl")
                        },
                        onError = { error ->
                            isLoading = false
                            errorMessage = error
                            Log.e("LicenseUpload", "Lisans yükleme hatası: $error")
                        }
                    )
                }
            }
        }
    )

    AlertDialog(
        onDismissRequest = {
            errorMessage = null // Hata mesajını sıfırla
            onDismiss()
        },
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
                    leadingIcon = {
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
                    leadingIcon = {
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
                    leadingIcon = {
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
                    leadingIcon = {
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
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Experience ikonu"
                        )
                    },
                    label = { Text(stringResource(R.string.experience_years)) },
                    isError = experienceYearError != null,
                    singleLine = true
                )
                if (experienceYearError != null) {
                    Text(experienceYearError!!, color = Color.Red, fontSize = 12.sp)
                }

                // Lisans görseli yükleme butonu
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lisans Görseli Değiştir",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(onClick = {
                        licenseImagePickerLauncher.launch("image/*") // Galeriyi aç
                    }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit License",
                            tint = Color(0xFFAD1457)
                        )
                    }
                }


                // Yükleme durumu veya görsel önizlemesi
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                // Hata mesajı
                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
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
                    experienceYearError = "Bu alan boş olamaz"
                    hasError = true
                } else {
                    try {
                        val experience = experienceYear.toInt()
                        if (experience < 0 || experience > 50) {
                            experienceYearError = "Deneyim yılı 0 ile 50 arasında olmalıdır"
                            hasError = true
                        }
                    } catch (e: NumberFormatException) {
                        experienceYearError = "Geçerli bir sayı girin"
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
                        licenseUrl = licenseUrl, // Güncellenmiş licenseUrl
                        experienceYear = experienceYear.toInt()
                    )
                    onConfirm(request)
                }
            }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                errorMessage = null // Hata mesajını sıfırla
                onDismiss()
            }) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
