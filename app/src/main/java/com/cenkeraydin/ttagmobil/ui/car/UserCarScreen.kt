package com.cenkeraydin.ttagmobil.ui.car

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.cenkeraydin.ttagmobil.R
import com.cenkeraydin.ttagmobil.data.model.car.Car

@Composable
fun UserCarScreen(navHostController: NavHostController) {
    val viewModel: CarViewModel = viewModel()
    val carsUsers = viewModel.cars_users
    val error by viewModel.errorMessages


    LaunchedEffect(Unit) {
        viewModel.getCarsForUser()
    }


    Column(modifier = Modifier.fillMaxSize()) {


        if (error != null) {
            Text("Hata: $error", color = Color.Red, modifier = Modifier.padding(16.dp))
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            androidx.compose.material.Text(text = "Our Cars")
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
                    items(carsUsers) { car ->
                        CarCardUsers(car = car, navHostController = navHostController)
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

            }

        }
    }
}


@Composable
fun CarCardUsers(car: Car, navHostController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }
    val viewModel: CarViewModel = viewModel()
    val imageUrls = viewModel.getImageUrlsForCar(car)

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
                Text(stringResource(R.string.brand)+": ${car.carBrand}")
                Text(stringResource(R.string.model)+": ${car.carModel}")
                Text(stringResource(R.string.passenger_capacity)+": ${car.passengerCapacity}")
                Text(stringResource(R.string.luggage_capacity)+": ${car.luggageCapacity}")
                Text(stringResource(R.string.price) +": ${car.price}")
            }

            Spacer(modifier = Modifier.width(16.dp))

            AsyncImage(
                model = imageUrls.first(),
                contentDescription = "Araç Görseli",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
        }
    }

    if (showDialog) {
        CarImageDialogUsers(images = imageUrls, onDismiss = { showDialog = false })
    }
}


@Composable
fun CarImageDialogUsers(images: List<String>, onDismiss: () -> Unit) {
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
            }
        }
    )
}