package com.cenkeraydin.ttagmobil.ui.car

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.cenkeraydin.ttagmobil.R
import com.cenkeraydin.ttagmobil.data.model.car.Car
import com.cenkeraydin.ttagmobil.data.model.car.CarCreateRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun DriverCarScreen(navHostController: NavHostController) {
    val viewModel: CarViewModel = viewModel()
    val cars = viewModel.cars.value
    val context = LocalContext.current
    val error by viewModel.errorMessages
    var showDialog by remember { mutableStateOf(false) }
    val prefs= context.getSharedPreferences("driver_prefs", Context.MODE_PRIVATE)
    val id= prefs.getString("id", null)


    LaunchedEffect(Unit) {
        viewModel.getCarsForDriver(context)
    }

    Column(modifier = Modifier.fillMaxSize()) {


        if (error != null) {
            Text("Hata: $error", color = Color.Red, modifier = Modifier.padding(16.dp))
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            androidx.compose.material.Text(text = "Your Cars")
                        }, actions = {
                            IconButton(onClick = {
                                showDialog = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Car",
                                    tint = Color.White
                                )
                            }
                        },
                        backgroundColor = Color.Black,
                        contentColor = Color.White
                    )
                }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding() // Alt bar ile çakışmaması için
                        .padding(innerPadding), // BottomNav yüksekliği kadar boşluk
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(cars) { car ->
                        CarCard(car = car, navHostController = navHostController)
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp)) // Liste sonunda boşluk
                        Text(
                            text = stringResource(R.string.more_coming),
                            color = Color.Gray,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                if (showDialog) {
                    AddCarDialog(
                        driverId = id!!,
                        onDismiss = { showDialog = false },
                        viewModel = viewModel
                    )
                }

            }

        }


    }
}



@Composable
fun CarCard(car: Car, navHostController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val imageUrls = car.imageUrls

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { showDialog = true },
        elevation = CardDefaults.cardElevation()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.brand) +": ${car.carBrand}")
                Text(stringResource(R.string.model) +": ${car.carModel}")
                Text(stringResource(R.string.passenger_capacity) +": ${car.passengerCapacity}")
                Text(stringResource(R.string.luggage_capacity) +": ${car.luggageCapacity}")
                Text(stringResource(R.string.price) +": ${car.price}")
            }

            Spacer(modifier = Modifier.width(16.dp))

            val firstImage = imageUrls.firstOrNull()

            if (firstImage != null) {
                AsyncImage(
                    model = firstImage,
                    contentDescription = "Araç Görseli",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Görsel yok", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }

    if (showDialog) {
        CarImageDialog(images = imageUrls,car.id,onDismiss = { showDialog = false })
    }
}


@Composable
fun CarImageDialog(
    images: List<String>,
    carId: String,
    onDismiss: () -> Unit,
    viewModel: CarViewModel = viewModel()
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val carImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }

            CoroutineScope(Dispatchers.IO).launch {
                viewModel.uploadCarImage(
                    bitmap = bitmap,
                    context = context,
                    carId = carId,
                )
            }
        }
    }



    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        },
        title = { Text(stringResource(R.string.car_images)) },
        text = {
            Column {
                if (images.isNotEmpty()) {
                    images.forEach { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = "Araç Fotoğrafı",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(vertical = 4.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Text("Görsel bulunamadı.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { carImagePickerLauncher.launch("image/*") }) {
                        Text("Görsel Ekle")
                    }
                }

                selectedImageUri?.let {
                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it, color = Color.Red)
                    }
                }
            }
        }
    )
}


@Composable
fun AddCarDialog(
    driverId: String,
    onDismiss: () -> Unit,
    viewModel: CarViewModel
) {
    var carBrand by remember { mutableStateOf("") }
    var carModel by remember { mutableStateOf("") }
    var passengerCapacity by remember { mutableStateOf("") }
    var luggageCapacity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Araç Ekle") },
        text = {
            Column {
                OutlinedTextField(value = carBrand, onValueChange = { carBrand = it }, label = { Text("Marka") })
                OutlinedTextField(value = carModel, onValueChange = { carModel = it }, label = { Text("Model") })
                OutlinedTextField(value = passengerCapacity, onValueChange = { passengerCapacity = it }, label = { Text("Yolcu Kapasitesi") })
                OutlinedTextField(value = luggageCapacity, onValueChange = { luggageCapacity = it }, label = { Text("Bagaj Kapasitesi") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Fiyat") })
                // Fotoğraf Seçme Butonu

                errorMessage?.let {
                    Text(text = it, color = Color.Red)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isLoading = true
                    val request = CarCreateRequest(
                        driverId = driverId,
                        carBrand = carBrand,
                        carModel = carModel,
                        passengerCapacity = passengerCapacity.toIntOrNull() ?: 0,
                        luggageCapacity = luggageCapacity.toIntOrNull() ?: 0,
                        price = price.toIntOrNull() ?: 0
                    )
                    viewModel.addCar(
                        carRequest = request,
                        onSuccess = {
                            isLoading = false
                            onDismiss()
                            Toast.makeText(context, "Araç eklendi", Toast.LENGTH_SHORT).show()
                        },
                        onError = { error ->
                            isLoading = false
                            errorMessage = error
                        }
                    )
                },
                enabled = !isLoading
            ) {
                Text("Ekle")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

