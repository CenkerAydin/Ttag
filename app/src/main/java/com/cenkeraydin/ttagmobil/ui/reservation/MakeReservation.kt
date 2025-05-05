package com.cenkeraydin.ttagmobil.ui.reservation

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.cenkeraydin.ttagmobil.data.model.car.Car
import com.cenkeraydin.ttagmobil.util.UserPrefsHelper


@Composable
fun MakeReservationScreen(
    startDate: String,
    startHour: String,
    endDate: String,
    endHour: String,
    fromWhere: String,
    toWhere: String,
    km: Int,
    viewModel: ReservationViewModel,
    navHostController: NavHostController
) {
    val drivers = viewModel.selectedDriver.collectAsState().value
    val cars = viewModel.cars.collectAsState().value
    var selectedCar by remember { mutableStateOf<Car?>(null) }
    val context = LocalContext.current
    val reservationSuccess = viewModel.reservationSuccess.collectAsState().value

    LaunchedEffect(reservationSuccess) {
        if (reservationSuccess) {
            navHostController.navigate("home")
            Toast.makeText(
                context,
                "Rezervasyon başarıyla oluşturuldu.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val userId= UserPrefsHelper(context).getUserId()
    Scaffold(
        bottomBar = {
            Button(
                onClick = { viewModel.createReservation(
                    drivers?.driverId,
                    userId,
                    startDate,
                    startHour,
                    endDate,
                    endHour,
                    fromWhere,
                    toWhere,
                    selectedCar?.price?.plus(km * 2)
                )  },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Rezervasyonu Onayla")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Rezervasyon Bilgileri", style = MaterialTheme.typography.h6)
                InfoGrid(
                    listOf(
                        "Sürücü Adı" to (drivers?.firstName ?: ""),
                        "Sürücü Soyadı" to (drivers?.lastName ?: ""),
                        "Başlangıç Tarihi" to startDate,
                        "Başlangıç Saati" to startHour,
                        "Bitiş Tarihi" to endDate,
                        "Bitiş Saati" to endHour,
                        "Nereden" to fromWhere,
                        "Nereye" to toWhere,
                        "Kilometre" to "$km km"
                    )
                )
                Text("Arabalar", style = MaterialTheme.typography.h6)
            }

            items(cars) { car ->
                CarItem(
                    car = car,
                    isSelected = selectedCar == car,
                    onClick = {
                        selectedCar = if (selectedCar == car) null else car
                    },
                    km = km
                )
            }
        }
    }
}


@Composable
fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.body2, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.body1)
    }
}

@Composable
fun InfoGrid(pairs: List<Pair<String, String>>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (i in pairs.indices step 2) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoRow(
                    label = pairs[i].first,
                    value = pairs[i].second,
                    modifier = Modifier.weight(1f)
                )
                if (i + 1 < pairs.size) {
                    InfoRow(
                        label = pairs[i + 1].first,
                        value = pairs[i + 1].second,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f)) // boşluk dengeleme
                }
            }
        }
    }
}


@Composable
fun CarItem(
    car: Car,
    km: Int,
    isSelected: Boolean,
    onClick: () -> Unit,

) {
    val totalPrice = car.price + (km * 2)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        backgroundColor = if (isSelected) Color(0xFFE0E0E0) else Color.White,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = car.carBrand, style = MaterialTheme.typography.h6)
                Text(text = car.carModel, style = MaterialTheme.typography.h6)
                Text(text = "Baz Fiyat: ${car.price} ₺", style = MaterialTheme.typography.body2)
            }
            Text(
                text = ("Toplam: $totalPrice"),
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold
            )

        }
    }
}


