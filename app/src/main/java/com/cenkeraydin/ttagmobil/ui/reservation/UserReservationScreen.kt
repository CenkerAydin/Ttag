package com.cenkeraydin.ttagmobil.ui.reservation


import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cenkeraydin.ttagmobil.R
import com.cenkeraydin.ttagmobil.components.AutoCompleteTextField
import com.cenkeraydin.ttagmobil.data.model.account.AvailableDriver
import com.cenkeraydin.ttagmobil.util.createDateTime
import com.cenkeraydin.ttagmobil.util.isValidDate
import com.cenkeraydin.ttagmobil.util.isValidHour
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import okhttp3.OkHttpClient
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter
import kotlin.math.round


@Composable
fun UserReservationScreen(navHostController: NavHostController,viewModel: ReservationViewModel) {
    val gold = Color(0xFFD4AF37)
    val backgroundColor = Color(0xFF1E1E1E)

    var startDate by remember { mutableStateOf("") }
    var startHour by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var endHour by remember { mutableStateOf("") }
    var fromWhere by remember { mutableStateOf("") }
    var toWhere by remember { mutableStateOf("") }
    var startDateError by remember { mutableStateOf(false) }
    var startHourError by remember { mutableStateOf(false) }
    val travelDuration by viewModel.travelDuration.collectAsState()
    val distanceInKm by viewModel.distanceInKm.collectAsState()

    val drivers by viewModel.drivers.observeAsState(emptyList())
    val error by viewModel.error.observeAsState(null)

    // Distance Matrix API için servis oluştur
    val client = remember { OkHttpClient() }
    val context = LocalContext.current
    val apiKey = remember { getApiKeyFromManifest(context) } // Manifest'ten API anahtarını al
    // Mesafe ve süreyi hesapla
    LaunchedEffect(fromWhere, toWhere) {
        if (fromWhere.isNotBlank() && toWhere.isNotBlank()) {
            viewModel.getDistanceAndDuration(fromWhere, toWhere, apiKey,client)
            Log.e("DEBUG", "fromWhere: $fromWhere, toWhere: $toWhere")

        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.reservation),
            color = Color.White,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
        Divider(
            color = Color.White,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .width(120.dp)
        )

        // Rezervasyon formu
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            ReservationScreens(
                startDate = startDate,
                onStartDateChange = { newStartDate ->
                    startDate = newStartDate
                    startDateError = !isValidDate(newStartDate)
                },
                startHour = startHour,
                onStartHourChange = { newStartHour ->
                    startHour = newStartHour
                    startHourError = !isValidHour(newStartHour)
                },
                fromWhere = fromWhere,
                onFromWhereChange = { fromWhere = it },
                toWhere = toWhere,
                onToWhereChange = { toWhere = it },
                startDateError = startDateError,
                startHourError = startHourError,
                onSearchClick = {
                    if (!startDateError && !startHourError) {
                        val startDateTime = createDateTime(startDate, startHour)
                        Log.e("Reservation", "Start DateTime: $startDateTime")
                        if (startDateTime != null) {

                            // Süreyi saniyeden saate çevir ve startDateTime üzerine ekle
                            val durationInHours = (round(travelDuration / 60.0)).toInt()
                            val endDateTime = startDateTime.plusHours(durationInHours.toLong())
                            Log.e("Reservation", "Travel Duration: $durationInHours Hours")
                            Log.e("Reservation", "End DateTime: $endDateTime")

                            // endDateTime'dan endDate ve endHour'u türet
                            endDate = endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            endHour = endDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))


                            viewModel.fetchAvailableDrivers(startDateTime.toString(),
                                endDateTime.toString()
                            )
                        } else {
                            Toast.makeText(
                                navHostController.context,
                                "Geçersiz tarih veya saat formatı",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.e("Reservation", "Geçersiz tarih veya saat formatı")
                        }
                    }
                }
            )

            // Hata mesajı
            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Sürücü listesi
            if (drivers.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.available_drivers),
                    color = gold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                drivers.forEach { driver ->
                    DriverCard(driver = driver, onClick = {
                        viewModel.selectDriver(driver)
                        val encodedFrom = URLEncoder.encode(fromWhere, StandardCharsets.UTF_8.toString())
                        val encodedTo = URLEncoder.encode(toWhere, StandardCharsets.UTF_8.toString())
                        navHostController.navigate("makeReservation/$startDate/$startHour/$endDate/$endHour/$encodedFrom/$encodedTo/$distanceInKm")
                    })
                }
            }
        }
    }
}

@Composable
fun ReservationScreens(
    startDate: String,
    onStartDateChange: (String) -> Unit,
    startHour: String,
    onStartHourChange: (String) -> Unit,
    fromWhere: String,
    onFromWhereChange: (String) -> Unit,
    toWhere: String,
    onToWhereChange: (String) -> Unit,
    startDateError: Boolean,
    startHourError: Boolean,
    onSearchClick: () -> Unit
) {
    val gold = Color(0xFFD4AF37)
    val backgroundColor = Color(0xFF1E1E1E)
    val context = LocalContext.current
    val apiKey = remember { getApiKeyFromManifest(context) }

    val placesClient = remember {
        if (apiKey != null) {
            if (!Places.isInitialized()) {
                Places.initialize(context.applicationContext, apiKey)
            }
            Places.createClient(context.applicationContext)
        } else null
    }
    DisposableEffect(placesClient) {
        onDispose {
            // Eğer ek bir temizleme veya kapanma işlemi gerekiyorsa buraya eklenebilir.
            Log.d("PlacesClient", "Places Client disposed")
        }
    }


    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }
    var fromSearchText by remember { mutableStateOf(fromWhere) }
    var toSearchText by remember { mutableStateOf(toWhere) }
    var fromPredictions by remember { mutableStateOf(listOf<AutocompletePrediction>()) }
    var toPredictions by remember { mutableStateOf(listOf<AutocompletePrediction>()) }



    LaunchedEffect(fromSearchText) {
        if (fromSearchText.isNotEmpty()) {
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(fromSearchText)
                .setCountry("TR") // Türkiye ile filtrele
                .setLocationBias(
                    RectangularBounds.newInstance(
                        LatLng(36.0, 29.5), // Antalya civarı (SW)
                        LatLng(37.0, 32.0)  // Antalya civarı (NE)
                    ))
                .build()

            placesClient?.findAutocompletePredictions(request)
                ?.addOnSuccessListener { response ->
                    fromPredictions = response.autocompletePredictions
                    fromExpanded = fromPredictions.isNotEmpty()
                }
                ?.addOnFailureListener { }
        } else {
            fromPredictions = emptyList()
            fromExpanded = false
        }
    }

    LaunchedEffect(toSearchText) {
        if (toSearchText.isNotEmpty()) {
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(toSearchText)
                .setCountry("TR") // Türkiye ile filtrele
                .setLocationBias(
                    RectangularBounds.newInstance(
                        LatLng(36.0, 29.5), // Antalya civarı (SW)
                        LatLng(37.0, 32.0)  // Antalya civarı (NE)
                    ))
                .build()

            placesClient?.findAutocompletePredictions(request)
                ?.addOnSuccessListener { response ->
                    toPredictions = response.autocompletePredictions
                    toExpanded = toPredictions.isNotEmpty()
                }
                ?.addOnFailureListener { }
        } else {
            toPredictions = emptyList()
            toExpanded = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Text(
            text = "For Reservation",
            color = gold,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Start Date and Start Hour
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = startDate,
                onValueChange = onStartDateChange,
                label = { Text(stringResource(R.string.date), color = Color.White) },
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = gold,
                    unfocusedBorderColor = gold,
                    textColor = Color.White,
                    placeholderColor = Color.Gray,
                    cursorColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                isError = startDateError
            )

            OutlinedTextField(
                value = startHour,
                onValueChange = onStartHourChange,
                label = { Text(stringResource(R.string.hour), color = Color.White) },
                leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = gold,
                    unfocusedBorderColor = gold,
                    textColor = Color.White,
                    placeholderColor = Color.Gray,
                    cursorColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                isError = startHourError
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Spacer(modifier = Modifier.height(12.dp))

        // From Where & To Where
        Column {
            AutoCompleteTextField(
                value = fromSearchText,
                onValueChange = {
                    fromSearchText = it
                    onFromWhereChange(it)
                    fromExpanded = true
                },
                suggestions = fromPredictions.map { it.getPrimaryText(null).toString() },
                showSuggestions = fromExpanded,
                onSuggestionSelected = {
                    fromSearchText = it
                    onFromWhereChange(it)
                    fromExpanded = false
                },
                label = stringResource(R.string.from),
                gold = gold
            )


            Spacer(modifier = Modifier.height(12.dp))

            AutoCompleteTextField(
                value = toSearchText,
                onValueChange = {
                    toSearchText = it
                    onToWhereChange(it)
                    toExpanded = true
                },
                suggestions = toPredictions.map { it.getPrimaryText(null).toString() },
                showSuggestions = toExpanded,
                onSuggestionSelected = {
                    toSearchText = it
                    onToWhereChange(it)
                    toExpanded = false
                },
                label = stringResource(R.string.to),
                gold = gold
            )

        }

        Spacer(modifier = Modifier.height(24.dp))

        // Search Button
        Button(
            onClick = onSearchClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = gold),
            shape = RoundedCornerShape(8.dp),
            enabled = !startDateError && !startHourError
        ) {
            Text(
                text = stringResource(R.string.search_reservations),
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }

        // Hata mesajları
        if (startDateError) {
            Text(
                text = stringResource(R.string.date_error),
                color = Color.Red,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
        if (startHourError) {
            Text(
                text = "Invalid start hour format (use HH:mm)",
                color = Color.Red,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun DriverCard(driver: AvailableDriver, onClick: () -> Unit) {
    // Yalnızca araç listesi boş değilse render et
    if (driver.cars.isNotEmpty()) {
        val gold = Color(0xFFD4AF37)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { onClick() },
            colors = CardColors(
                containerColor = gold.copy(alpha = 0.2f),
                contentColor = Color.White,
                disabledContainerColor = Color.Gray.copy(alpha = 0.2f),
                disabledContentColor = Color.Gray
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "${driver.firstName} ${driver.lastName}",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                driver.cars.forEach { car ->
                    Text(
                        text = "Araç: ${car.carBrand} ${car.carModel} - Fiyat: ${car.price}₺",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Kapasite: ${car.passengerCapacity} kişi, ${car.luggageCapacity} bagaj",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

fun getApiKeyFromManifest(context: Context): String? {
    return try {
        val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        val metaData = appInfo.metaData
        metaData.getString("com.google.android.geo.API_KEY")
    } catch (e: PackageManager.NameNotFoundException) {
        Log.e("ApiKey", "Failed to load meta-data, NameNotFound: ${e.message}")
        null
    } catch (e: Exception) {
        Log.e("ApiKey", "Failed to load meta-data: ${e.message}")
        null
    }
}

