package com.cenkeraydin.ttagmobil.ui.profile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.cenkeraydin.ttagmobil.R
import com.cenkeraydin.ttagmobil.components.ReadOnlyTextField
import com.cenkeraydin.ttagmobil.components.UpdateInfoButton
import com.cenkeraydin.ttagmobil.data.model.account.User
import com.cenkeraydin.ttagmobil.util.UpdateUserDialog
import com.cenkeraydin.ttagmobil.util.UserPrefsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun PassengerProfileScreen(user: User?) {
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel()
    var showDialog by remember { mutableStateOf(false) }

    var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        profileBitmap = UserPrefsHelper(context).getProfileImage()
        profileImageUrl = user?.pictureUrl // Gelen URL

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

                val userId = UserPrefsHelper(context).getUserId()


                CoroutineScope(Dispatchers.IO).launch {
                    profileViewModel.uploadProfilePicture(bitmap, context, userId)
                }

            }
        }
    )
    Box(
        modifier = Modifier
            .size(140.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFBB86FC), Color(0xFF958BA2))
                )
            )
            .border(3.dp, Color.White, CircleShape)
            .clickable {
                imagePickerLauncher.launch("image/*")
            }
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
            user?.let {
                it.firstName?.let { it1 ->
                    ReadOnlyTextField(
                        label = stringResource(R.string.name),
                        value = it1,
                        leadingIcon = Icons.Default.Person
                    )
                }
                it.lastName?.let { it1 ->
                    ReadOnlyTextField(
                        label = stringResource(R.string.surname),
                        value = it1,
                        leadingIcon = Icons.Default.Person
                    )
                }
                it.phoneNumber?.let { it1 ->
                    ReadOnlyTextField(
                        label = stringResource(R.string.phone),
                        value = it1,
                        leadingIcon = Icons.Default.Phone
                    )
                }
                it.email?.let { it1 ->
                    ReadOnlyTextField(
                        label = stringResource(R.string.email),
                        value = it1,
                        leadingIcon = Icons.Filled.Email
                    )
                }
            } ?: CircularProgressIndicator()

            UpdateInfoButton {
                showDialog = true
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

