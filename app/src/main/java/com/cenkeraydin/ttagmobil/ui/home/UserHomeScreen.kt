package com.cenkeraydin.ttagmobil.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.cenkeraydin.ttagmobil.ui.reservation.ReservationViewModel
import com.cenkeraydin.ttagmobil.util.UserPrefsHelper
import com.cenkeraydin.ttagmobil.util.formatReservationDate

@Composable
fun UserHomeScreen(viewModel: ReservationViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val userName = UserPrefsHelper(context).getUserName()
    val currentUserId = UserPrefsHelper(context).getUserId()

    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(key1 = lifecycleOwner.value.lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchUserReservations(currentUserId)
            }
        }

        lifecycleOwner.value.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.value.lifecycle.removeObserver(observer)
        }
    }

    val userReservations by viewModel.userReservations.collectAsState()

    Column(modifier = modifier) { // Buraya modifier parametresi eklendi
        Text(
            text = "Welcome $userName 👋",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        val sortedReservations = userReservations.sortedBy {
            when (it.status) {
                0 -> 0
                4 -> 1
                else -> 2
            }
        }

        LazyColumn {
            items(sortedReservations) { reservation ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Reservation Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Formatlama fonksiyonunu çağırıyoruz.
                        val (startDate, startTime) = formatReservationDate(reservation.startDateTime)
                        val (endDate, endTime) = formatReservationDate(reservation.endDateTime)

                        Text("🚘 Sürücü: ${reservation.driverFirstName} ${reservation.driverLastName}", style = MaterialTheme.typography.bodyMedium)
                        Text("📍 Nereden: ${reservation.fromWhere}", style = MaterialTheme.typography.bodyMedium)
                        Text("🏁 Nereye: ${reservation.toWhere}", style = MaterialTheme.typography.bodyMedium)
                        Text("🗓️ Başlangıç: $startDate", style = MaterialTheme.typography.bodyMedium)
                        Text("🕒 Başlangıç Saati: $startTime", style = MaterialTheme.typography.bodyMedium)
                        Text("🗓️ Bitiş: $endDate", style = MaterialTheme.typography.bodyMedium)
                        Text("🕒 Bitiş Saati: $endTime", style = MaterialTheme.typography.bodyMedium)
                        Text("💸 Ücret: ${reservation.price} ₺", style = MaterialTheme.typography.bodyMedium)


                        val statusColor = when (reservation.status) {
                            0 -> Color.Black    // Pending
                            1 -> Color.Green    // Approved
                            3, 4 -> Color.Red   // Declined & Canceled
                            else -> Color.Gray  // Diğer durumlar için
                        }
                        Text(
                            text = when (reservation.status) {
                                0 -> "Durum: Beklemede"
                                1 -> "Durum: Onaylandı!"
                                2 -> "Durum: Tamamlandı"
                                3-> "Durum: İptal Edildi"
                                4 -> "Durum: İptal Edildi"
                                else -> "Durum: Bilinmiyor"
                            },
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )

                        if (reservation.status == 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    viewModel.cancelReservation(reservation.id, context)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(
                                    0xFFC62828
                                )
                                ),
                            ) {
                                Text("İptal Et", color = Color.White)
                            }
                        }
                    }
                }

            }
        }
    }
}


