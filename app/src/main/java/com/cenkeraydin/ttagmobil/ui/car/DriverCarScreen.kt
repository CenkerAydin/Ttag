package com.cenkeraydin.ttagmobil.ui.car

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.cenkeraydin.ttagmobil.R
import com.cenkeraydin.ttagmobil.data.model.car.Car
import com.cenkeraydin.ttagmobil.data.model.car.CarCreateRequest
import kotlinx.coroutines.launch


@Composable
fun DriverCarScreen(navHostController: NavHostController) {
    val viewModel: CarViewModel = viewModel()
    val cars = viewModel.cars.value
    val context = LocalContext.current
    val error by viewModel.errorMessages.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val prefs = context.getSharedPreferences("driver_prefs", Context.MODE_PRIVATE)
    val id = prefs.getString("id", null)

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
                            androidx.compose.material.Text(text = stringResource(R.string.your_cars))
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
                        viewModel = viewModel,
                    )
                }

            }

        }


    }
}


@Composable
fun CarCard(
    car: Car,
    navHostController: NavHostController,
    viewModel: CarViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val imageUrls = car.imageUrls
    val firstImage = imageUrls.firstOrNull()
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { showDialog = true },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Araba Görseli
            if (firstImage != null) {
                AsyncImage(
                    model = firstImage,
                    contentDescription = "Araç Görseli",
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Görsel Yok", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
            ){
                Text(
                    text = "${stringResource(R.string.brand)}: ${car.carBrand}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text("${stringResource(R.string.model)}: ${car.carModel}")
                Text("${stringResource(R.string.passenger_capacity)}: ${car.passengerCapacity}")
                Text("${stringResource(R.string.luggage_capacity)}: ${car.luggageCapacity}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${stringResource(R.string.price)}: \$${car.price}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E88E5)
                    )
                )
            }

            // Icon yukarı değil, orta hizalı görünsün
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = { showDeleteDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Sil",
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

    }

    if (showDialog) {
        CarImageDialog(
            images = imageUrls,
            carId = car.id,
            onDismiss = { showDialog = false },
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_car_title)) },
            text = { Text(stringResource(R.string.delete_car_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCar(
                            carId = car.id,
                            onSuccess = {
                                Toast.makeText(context, context.getString(R.string.car_deleted), Toast.LENGTH_SHORT).show()
                                showDeleteDialog = false
                            },
                            onError = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                showDeleteDialog = false
                            }
                        )
                    }
                ) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.no))
                }
            }
        )
    }
}



@Composable
fun CarImageDialog(
    images: List<String>,
    carId: String,
    onDismiss: () -> Unit,
    viewModel: CarViewModel = viewModel(),
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState { images.size }

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

            isLoading = true
            coroutineScope.launch {
                viewModel.uploadCarImage(
                    bitmap = bitmap,
                    context = context,
                    carId = carId,
                    onSuccess = {
                        isLoading = false
                        errorMessage = null
                    },
                    onError = { error ->
                        isLoading = false
                        errorMessage = error
                    }
                )
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.car_images),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Görsel yükleme butonu


                // Yükleme göstergesi
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (images.isNotEmpty()) {
                    // Görseller için HorizontalPager
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    ) { page ->
                        AsyncImage(
                            model = images[page],
                            contentDescription = "Araç Fotoğrafı $page",
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFE0E0E0)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sayfa göstergesi
                    Text(
                        text = "${pagerState.currentPage + 1} / ${images.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                } else {
                    // Görsel yoksa
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Henüz görsel yüklenmemiş.",
                            color = Color.Gray
                        )
                    }
                }
                Button(
                    onClick = { carImagePickerLauncher.launch("image/*") },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF1E88E5),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text("Görsel Ekle")
                }

                // Hata mesajı
                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.close),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    )
}


@Composable
fun AddCarDialog(
    driverId: String,
    onDismiss: () -> Unit,
    viewModel: CarViewModel,
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
                OutlinedTextField(
                    value = carBrand,
                    onValueChange = { carBrand = it },
                    label = { Text("Marka") })
                OutlinedTextField(
                    value = carModel,
                    onValueChange = { carModel = it },
                    label = { Text("Model") })
                OutlinedTextField(
                    value = passengerCapacity,
                    onValueChange = { passengerCapacity = it },
                    label = { Text("Yolcu Kapasitesi") })
                OutlinedTextField(
                    value = luggageCapacity,
                    onValueChange = { luggageCapacity = it },
                    label = { Text("Bagaj Kapasitesi") })
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Fiyat") })

                errorMessage?.let {
                    Text(text = it, color = Color.Red)
                }
            }
        },
        confirmButton = {
            OutlinedButton(
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
                        },
                        context
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

