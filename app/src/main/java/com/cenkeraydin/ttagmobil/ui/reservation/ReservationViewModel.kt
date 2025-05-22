package com.cenkeraydin.ttagmobil.ui.reservation

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cenkeraydin.ttagmobil.data.model.account.AvailableDriver
import com.cenkeraydin.ttagmobil.data.model.car.Car
import com.cenkeraydin.ttagmobil.data.model.reservation.CreateReservationRequest
import com.cenkeraydin.ttagmobil.data.model.reservation.ReservationResponse
import com.cenkeraydin.ttagmobil.data.retrofit.RetrofitInstance
import com.cenkeraydin.ttagmobil.data.retrofit.RetrofitInstance.api
import com.cenkeraydin.ttagmobil.util.DriverPrefsHelper
import com.cenkeraydin.ttagmobil.util.PreferencesHelper
import com.cenkeraydin.ttagmobil.util.UserPrefsHelper
import com.cenkeraydin.ttagmobil.util.toIsoDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder

open class ReservationViewModel(application: Application) : AndroidViewModel(application) {

    private val _drivers = MutableStateFlow<List<AvailableDriver>>(emptyList())
    val drivers: StateFlow<List<AvailableDriver>> get() = _drivers

    private val _userReservations = MutableStateFlow<List<ReservationResponse>>(emptyList())
    val userReservations: StateFlow<List<ReservationResponse>> = _userReservations

    private val _driverReservations = MutableStateFlow<List<ReservationResponse>>(emptyList())
    val driverReservations: StateFlow<List<ReservationResponse>> = _driverReservations

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val preferencesHelper = PreferencesHelper(application.applicationContext)


    private val _selectedRole = MutableStateFlow(preferencesHelper.getSelectedRole())
    val selectedRole: StateFlow<String?> = _selectedRole

    private val _selectedDriver = MutableStateFlow<AvailableDriver?>(null)
    val selectedDriver: StateFlow<AvailableDriver?> = _selectedDriver

    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    val cars: StateFlow<List<Car>> = _cars

    private val _reservationSuccess = MutableStateFlow(false)
    val reservationSuccess: StateFlow<Boolean> = _reservationSuccess

    private val _distanceInKm = MutableStateFlow(0)
    val distanceInKm: StateFlow<Int> = _distanceInKm

    private val _travelDuration = MutableStateFlow(0)
    val travelDuration: StateFlow<Int> = _travelDuration

    private val api = RetrofitInstance.api


    fun selectDriver(driver: AvailableDriver) {
        _selectedDriver.value = driver
        _cars.value = driver.cars
    }

    fun fetchAvailableDrivers(startDateTime: String, endDateTime: String) {
        viewModelScope.launch {
            try {
                val response = api.getAvailableDrivers(startDateTime, endDateTime)
                Log.e("TAG", "Response: $response")
                if (response.isSuccessful) {
                    _drivers.value = response.body() ?: emptyList() // postValue yerine value =
                    _error.value = null
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Hata: ${response.code()} - $errorBody"
                }
            } catch (e: Exception) {
                _error.value = "Hata: ${e.message}"
            }
        }
    }

    fun clearDrivers() {
        _drivers.value=emptyList()
    }

    fun createReservation(
        driverId: String?,
        userId: String?,
        startDate: String?,
        startHour: String?,
        endDate: String?,
        endHour: String?,
        fromWhere: String?,
        toWhere: String?,
        price: Int?
    ) {
        viewModelScope.launch {
            try {
                val startDateTime = toIsoDateTime(startDate, startHour)
                val endDateTime = toIsoDateTime(endDate, endHour)
                val request= CreateReservationRequest(
                    driverId = driverId,
                    userId = userId,
                    startDateTime = startDateTime,
                    endDateTime = endDateTime,
                    fromWhere = fromWhere,
                    toWhere = toWhere,
                    price = price
                )

                val response = api.createReservation(request)
                if (response.isSuccessful) {
                    _reservationSuccess.value = true
                    Log.d("TAG", "Reservation created successfully")
                } else {
                    _reservationSuccess.value = false
                    Log.e("TAG", "Error creating reservation: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error creating reservation: ${e.message}")
            }
        }
    }

    fun getDistanceAndDuration(fromWhere: String, toWhere: String, apiKey:String?, client: OkHttpClient) {
        if (fromWhere.isNotEmpty() && toWhere.isNotEmpty()) {
            val url = "https://maps.googleapis.com/maps/api/distancematrix/json" +
                    "?origins=${URLEncoder.encode(fromWhere, "UTF-8")}" +
                    "&destinations=${URLEncoder.encode(toWhere, "UTF-8")}" +
                    "&mode=driving" +
                    "&units=metric" +
                    "&key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("DistanceMatrix", "Request failed: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body?.string()?.let { jsonString ->
                        val jsonObject = JSONObject(jsonString)
                        val status = jsonObject.getString("status")
                        if (status == "OK") {
                            val rows = jsonObject.getJSONArray("rows")
                            val elements = rows.getJSONObject(0).getJSONArray("elements")
                            val element = elements.getJSONObject(0)
                            val elementStatus = element.getString("status")
                            if (elementStatus == "OK") {
                                val distance = element.getJSONObject("distance")
                                val distanceInMeters = distance.getInt("value")
                                val km = (distanceInMeters / 1000.0).toInt()
                                _distanceInKm.value = km
                                Log.e("DistanceMatrix", "Distance: $km km")

                                val duration = element.getJSONObject("duration")
                                val durationInMinutes = (duration.getLong("value") /60) *2
                                _travelDuration.value = durationInMinutes.toInt()
                                Log.e("DistanceMatrix", "Travel duration: $durationInMinutes minutes")
                            } else {
                                Log.e("DistanceMatrix", "Element status: $elementStatus")
                            }
                        } else {
                            Log.e("DistanceMatrix", "Request status: $status")
                        }
                    }
                }
            })
        }
    }


    fun fetchUserReservations(userId: String?) {
        viewModelScope.launch {
            try {
                val result = api.getUserReservations(userId)
                Log.e("TAG API", "User Reservations: $result")
                _userReservations.value = result
            } catch (e: Exception) {
                Log.e("TAG API", "Error fetching user reservations: ${e.message}")
                _error.value = "Hata oluştu: ${e.message}"
            }
        }
    }

    fun fetchDriverReservations(driverId: String?){
        viewModelScope.launch {
            try {
                val result = api.getDriverReservations(driverId)
                Log.e("TAG API", "Driver Reservations: $result")
                _driverReservations.value = result
            } catch (e: Exception) {
                Log.e("TAG API", "Error fetching driver reservations: ${e.message}")
                _error.value = "Hata oluştu: ${e.message}"
            }
        }
    }

    fun cancelReservation(reservationId: String?, context: Context) {
        viewModelScope.launch {
            try {
                if (reservationId != null) {
                    Log.e("TAG", "İptal edilecek rezervasyon ID: $reservationId")
                    api.updateReservationStatus(reservationId, 4)
                    Log.e("TAG", "İptal işlemi başarılı")
                    // Güncel veriyi tekrar çek
                    fetchUserReservations(UserPrefsHelper(context).getUserId())
                }
            } catch (e: Exception) {
                Log.e("TAG", "İptal hatası: ${e.message}")
            }
        }
    }

    fun approvedReservation(reservationId: String?, context: Context){
        viewModelScope.launch {
            try {
                if (reservationId != null) {
                    api.updateReservationStatus(reservationId, 1)
                    Log.e("TAG", "Onay işlemi başarılı")
                    fetchDriverReservations(DriverPrefsHelper(context).getDriverId())
                }
            } catch (e: Exception) {
                Log.e("TAG", "Onay hatası: ${e.message}")
            }
        }
    }

    fun declinedReservation(reservationId: String?, context: Context){
        viewModelScope.launch {
            try {
                if (reservationId != null) {
                    api.updateReservationStatus(reservationId, 3)
                    Log.e("TAG", "Reddetme işlemi başarılı")
                    fetchDriverReservations(DriverPrefsHelper(context).getDriverId())
                }
            } catch (e: Exception) {
                Log.e("TAG", "Reddetme hatası: ${e.message}")
            }
        }
    }
    fun completedReservation(reservationId: String?, context: Context){
        viewModelScope.launch {
            try {
                if (reservationId != null) {
                    api.updateReservationStatus(reservationId, 2)
                    Log.e("TAG", "Tamamlama işlemi başarılı")
                    fetchDriverReservations(DriverPrefsHelper(context).getDriverId())
                }
            } catch (e: Exception) {
                Log.e("TAG", "Tamamlama hatası: ${e.message}")
            }
        }
    }



}