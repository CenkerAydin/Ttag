package com.cenkeraydin.ttagmobil.ui.profile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.cenkeraydin.ttagmobil.R
import com.cenkeraydin.ttagmobil.components.ReadOnlyTextField
import com.cenkeraydin.ttagmobil.components.UpdateInfoButton
import com.cenkeraydin.ttagmobil.data.model.account.Driver
import com.cenkeraydin.ttagmobil.util.DriverPrefsHelper
import com.cenkeraydin.ttagmobil.util.UpdateDriverDialog
import com.cenkeraydin.ttagmobil.util.UserPrefsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DriverProfileScreen(driver: Driver?) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel()

    var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    val uriHandler = LocalUriHandler.current
    var licenseUrl = driver?.licenseUrl ?: ""

    LaunchedEffect(Unit) {
        profileBitmap = DriverPrefsHelper(context).getProfileImage()
        profileImageUrl = driver?.pictureUrl
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
                val base64 = DriverPrefsHelper(context).encodeBitmapToBase64(bitmap)
                DriverPrefsHelper(context).saveProfileImage(base64)

                val userId = UserPrefsHelper(context).getUserId()

                CoroutineScope(Dispatchers.IO).launch {
                    profileViewModel.uploadProfilePicture(bitmap, context, userId)
                }
            }
        }
    )


    val licensePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }

                val userId = UserPrefsHelper(context).getUserId()

                CoroutineScope(Dispatchers.IO).launch {
                    val newUrl = profileViewModel.uploadDriverLicense(bitmap, context, userId)
                    newUrl.let {
                        withContext(Dispatchers.Main) {
                            licenseUrl = it.toString()
                            Toast.makeText(context, "Lisans güncellendi", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    )



    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFBB86FC), Color(0xFF958BA2))
                )
            )
            .border(3.dp, Color.White, CircleShape)
            .clickable { imagePickerLauncher.launch("image/*") }
            .shadow(8.dp, CircleShape, clip = false)
    ) {
        when {
            profileBitmap != null -> {
                Image(
                    bitmap = profileBitmap!!.asImageBitmap(),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            profileImageUrl != null -> {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Profile Picture From URL",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            else -> {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Default Profile Picture",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(64.dp),
                    tint = Color.White
                )
            }
        }
    }


    Column(modifier = Modifier.padding(16.dp)) {
        driver?.let {
            ReadOnlyTextField(
                label = stringResource(R.string.name),
                value = it.firstName,
                leadingIcon = Icons.Default.Person
            )
            ReadOnlyTextField(
                label = stringResource(R.string.surname),
                value = it.lastName,
                leadingIcon = Icons.Default.Person
            )
            it.email?.let { it1 ->
                ReadOnlyTextField(
                    label = stringResource(R.string.email),
                    value = it1,
                    leadingIcon = Icons.Default.Email
                )
            }
            ReadOnlyTextField(
                label = stringResource(R.string.experience_years),
                value = it.experienceYear.toString(),
                leadingIcon = Icons.Default.CheckCircle
            )

            licenseUrl.takeIf { it.isNotBlank() }?.let { licenseUrl ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                try {
                                    uriHandler.openUri(licenseUrl)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Geçerli bir bağlantı değil", Toast.LENGTH_SHORT).show()
                                }
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "License URL",
                            tint = Color(0xFF0D47A1)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "View License",
                            color = Color(0xFF0D47A1),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Edit License Button (galeri açılır)
                    IconButton(onClick = {
                        licensePickerLauncher.launch("image/*") // Galeriyi aç
                    }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit License",
                            tint = Color(0xFFAD1457)
                        )
                    }
                }
            }


            it.phoneNumber?.let { it1 ->
                ReadOnlyTextField(
                    label = stringResource(R.string.phone),
                    value = it1,
                    leadingIcon = Icons.Default.Phone
                )
            }
        } ?: CircularProgressIndicator()

        UpdateInfoButton {
            showDialog = true
        }

        if (showDialog && driver != null) {
            UpdateDriverDialog(
                initialEmail = driver.email,
                initialLicenseUrl = driver.licenseUrl ?: "",
                onDismiss = { showDialog = false },
                onConfirm = { request ->
                    showDialog = false
                    profileViewModel.updateDriverInfo(request, context)
                }
            )
        }
    }
}
