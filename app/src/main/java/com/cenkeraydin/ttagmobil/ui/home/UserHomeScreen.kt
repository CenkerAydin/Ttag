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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
            text = "${stringResource(R.string.welcome)} $userName 👋",
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
                    shape = RoundedCornerShape(8.dp),
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


        Spacer(modifier = Modifier.height(8.dp))

        val filteredReservations = userReservations
            .filter { selectedStatusFilter == null || it.status == selectedStatusFilter }
            .sortedBy {
                when (it.status) {
                    0 -> 0
                    4 -> 1
                    else -> 2
                }
            }

        if (filteredReservations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_available_reservations),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            LazyColumn {
                items(filteredReservations) { reservation ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = stringResource(R.string.reservation_details),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
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
                                0 -> stringResource(R.string.status_pending) to MaterialTheme.colorScheme.onSurfaceVariant
                                1 -> stringResource(R.string.status_approved) to MaterialTheme.colorScheme.primary
                                2 -> stringResource(R.string.status_completed) to MaterialTheme.colorScheme.tertiary
                                3 -> stringResource(R.string.status_rejected) to MaterialTheme.colorScheme.error
                                4 -> stringResource(R.string.status_cancelled) to MaterialTheme.colorScheme.error
                                else -> stringResource(R.string.unknown) to MaterialTheme.colorScheme.outline
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

                            if (reservation.status == 0) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.cancelReservation(reservation.id, context)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(
                                            0xFFC62828
                                        )
                                    ),
                                ) {
                                    Text(stringResource(R.string.cancel), color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

