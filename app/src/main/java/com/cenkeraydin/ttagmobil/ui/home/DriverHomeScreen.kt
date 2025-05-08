package com.cenkeraydin.ttagmobil.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.cenkeraydin.ttagmobil.R
import com.cenkeraydin.ttagmobil.ui.reservation.ReservationViewModel
import com.cenkeraydin.ttagmobil.util.DriverPrefsHelper
import com.cenkeraydin.ttagmobil.util.UserPrefsHelper
import com.cenkeraydin.ttagmobil.util.formatReservationDate

@Composable
fun DriverHomeScreen(viewModel: ReservationViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val driverName = DriverPrefsHelper(context).getDriverName()
    val currentDriverId = DriverPrefsHelper(context).getDriverId()

    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(key1 = lifecycleOwner.value.lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchDriverReservations(currentDriverId)
            }
        }

        lifecycleOwner.value.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.value.lifecycle.removeObserver(observer)
        }
    }

    val driverReservations by viewModel.driverReservations.collectAsState()

    var selectedStatusFilter by remember { mutableStateOf<Int?>(null) }

    val statusOptions = listOf(
        null to stringResource(id = R.string.status_all),
        0 to stringResource(id = R.string.status_pending),
        1 to stringResource(id = R.string.status_approved),
        2 to stringResource(id = R.string.status_completed),
        3 to stringResource(id = R.string.status_rejected),
        4 to stringResource(id = R.string.status_cancelled),
    )


    Column(modifier = modifier) {
        Text(
            text = "${stringResource(R.string.welcome)} $driverName 👋",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            statusOptions.distinctBy { it.first }.forEach { (status, label) ->
                val isSelected = selectedStatusFilter == status
                OutlinedButton(
                    onClick = { selectedStatusFilter = status },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) Color(0xFF2196F3) else Color.White,
                        contentColor = if (isSelected) Color.White else Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) Color(0xFF2196F3) else Color.LightGray
                    ),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                    )
                }
            }
        }
        val filteredReservations = driverReservations
            .filter { selectedStatusFilter == null || it.status == selectedStatusFilter }
            .sortedBy {
                when (it.status) {
                    0 -> 0
                    1-> 1
                    2 -> 2
                    3 -> 3
                    else -> 4
                }
            }

        LazyColumn {
            items(filteredReservations) { reservation ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = stringResource(R.string.reservation_details),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val (startDate, startTime) = formatReservationDate(reservation.startDateTime)
                        val (endDate, endTime) = formatReservationDate(reservation.endDateTime)
                        val fromWhere = reservation.fromWhere.replace("+"," ")
                        val toWhere = reservation.toWhere.replace("+"," ")
                        Text(stringResource(R.string.driver_label, reservation.driverFirstName, reservation.driverLastName), style = MaterialTheme.typography.bodyMedium)
                        Text(stringResource(R.string.from_label, fromWhere), style = MaterialTheme.typography.bodyMedium)
                        Text(stringResource(R.string.to_label, toWhere), style = MaterialTheme.typography.bodyMedium)
                        Text(stringResource(R.string.start_date_label, startDate), style = MaterialTheme.typography.bodyMedium)
                        Text(stringResource(R.string.start_time_label, startTime), style = MaterialTheme.typography.bodyMedium)
                        Text(stringResource(R.string.end_date_label, endDate), style = MaterialTheme.typography.bodyMedium)
                        Text(stringResource(R.string.end_time_label, endTime), style = MaterialTheme.typography.bodyMedium)
                        Text(stringResource(R.string.price_label, reservation.price), style = MaterialTheme.typography.bodyMedium)


                        Spacer(modifier = Modifier.height(12.dp))

                        // Duruma göre renkli etiket
                        val (statusText, statusColor) = when (reservation.status) {
                            0 -> stringResource(R.string.status_pending) to Color(0xFF424242)
                            1 -> stringResource(R.string.status_approved) to Color(0xFF2E7D32)
                            2 -> stringResource(R.string.status_completed) to Color(0xFF1565C0)
                            3 -> stringResource(R.string.status_rejected) to Color(0xFFC62828)
                            4 -> stringResource(R.string.status_cancelled) to Color(0xFFC62828)
                            else -> stringResource(R.string.unknown) to Color.Gray
                        }

                        Box(
                            modifier = Modifier
                                .background(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.status_prefix, statusText),
                                color = statusColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (reservation.status == 0) {
                            Row {
                                Button(
                                    onClick = {
                                        viewModel.approvedReservation(reservation.id, context)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(stringResource(R.string.approve), color = Color.White)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        viewModel.declinedReservation(reservation.id, context)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(stringResource(R.string.decline), color = Color.White)
                                }
                            }
                        }

                        if (reservation.status == 1) {
                            Button(
                                onClick = {
                                    viewModel.completedReservation(reservation.id, context)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.completed), color = Color.White)
                            }
                        }
                    }
                }

            }
                }
            }
        }

