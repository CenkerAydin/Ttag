package com.cenkeraydin.ttagmobil.ui.car

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cenkeraydin.ttagmobil.data.model.car.Car
import com.cenkeraydin.ttagmobil.data.model.car.CarCreateRequest
import com.cenkeraydin.ttagmobil.data.retrofit.RetrofitInstance
import com.cenkeraydin.ttagmobil.util.PreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class CarViewModel(application: Application) : AndroidViewModel(application) {

    private val _cars = mutableStateOf<List<Car>>(emptyList())
    val cars: State<List<Car>> = _cars

    var cars_users by mutableStateOf<List<Car>>(emptyList())
        private set

    private var _errorMessages = mutableStateOf<String?>(null)
    val errorMessages: State<String?> = _errorMessages

    private val preferencesHelper = PreferencesHelper(application.applicationContext)

    private val _selectedRole = MutableStateFlow(preferencesHelper.getSelectedRole())
    val selectedRole: StateFlow<String?> = _selectedRole

    fun getCarsForDriver(context: Context) {
        viewModelScope.launch {
            try {

                val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val email = prefs.getString("email", null)

                val response = email?.let { RetrofitInstance.api.getDriverInfo(it) }
                if (response != null) {
                    if (response.isSuccessful) {
                        response.body()?.let { driverInfo ->
                            _cars.value = driverInfo.data.cars
                        }
                    } else {
                        Log.e("CarVM", "Error: ${response.code()}")
                        _errorMessages.value = "Error: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                _errorMessages.value = "Error: ${e.localizedMessage}"
                Log.e("CarVM", "Exception: ${e.localizedMessage}")
            }
        }
    }

    fun getCarsForUser() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getCars()
                if (response.isSuccessful) {
                    cars_users = response.body()?.data ?: emptyList()
                } else {
                    _errorMessages.value = response.errorBody()?.string()
                }
            } catch (e: Exception) {
                _errorMessages.value = e.localizedMessage
            }
        }
    }

    fun addCar(
        carRequest: CarCreateRequest,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.addCar(carRequest)
                Log.e("CarVM", "Response: ${response.body()}")
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Hata kodu: ${response.code()}")
                }
            } catch (e: Exception) {
                onError("İstisna: ${e.localizedMessage}")
            }
        }
    }

    fun getImageUrlsForCar(car: Car): List<String> {
        return when (car.carModel.lowercase()) {
            "vito" -> listOf(
                "https://ttagstorage.blob.core.windows.net/ttagupload/vito1.jpg",
                "https://ttagstorage.blob.core.windows.net/ttagupload/vito2.jpg",
                "https://ttagstorage.blob.core.windows.net/ttagupload/vito3.jpg"
            )

            "cla" -> listOf(
                "https://ttagstorage.blob.core.windows.net/ttagupload/cla1.jpg",
                "https://ttagstorage.blob.core.windows.net/ttagupload/cla2.jpg",
                "https://ttagstorage.blob.core.windows.net/ttagupload/cla3.jpg"

            )

            "g63" -> listOf(
                "https://ttagstorage.blob.core.windows.net/ttagupload/g631.jpg",
                "https://ttagstorage.blob.core.windows.net/ttagupload/g632.jpg",
                "https://ttagstorage.blob.core.windows.net/ttagupload/g633.jpg"
            )

            else -> listOf("https://ralfvanveen.com/en/glossary/placeholder/") // default görsel
        }
    }

    suspend fun uploadCarImage(
        bitmap: Bitmap,
        context: Context,
        carId: String
    ) {
        // Görseli geçici bir dosyaya kaydet
        val file = File(context.cacheDir, "car_image_${System.currentTimeMillis()}.jpg")
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }

        // Görseli multipart olarak hazırlıyoruz
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("Image", file.name, requestFile)
        val carIdPart = carId.toRequestBody("text/plain".toMediaTypeOrNull())
        val response = RetrofitInstance.api.uploadCarImage(imagePart, carIdPart)

        if (response.isSuccessful) {
            Log.d("Upload", "Başarılı")
        } else {
            Log.e("Upload", "CarId $carId")
            Log.e("Upload", "Hata: ${response.message()}")
            Log.e("Upload", "Hata: ${response.code()}")
        }
    }

}



