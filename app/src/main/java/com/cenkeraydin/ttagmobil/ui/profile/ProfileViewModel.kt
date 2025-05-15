package com.cenkeraydin.ttagmobil.ui.profile

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.cenkeraydin.ttagmobil.data.model.account.Driver
import com.cenkeraydin.ttagmobil.data.model.account.UpdateDriverInfoRequest
import com.cenkeraydin.ttagmobil.data.model.account.UpdateUserInfoRequest
import com.cenkeraydin.ttagmobil.data.model.account.User
import com.cenkeraydin.ttagmobil.data.model.car.Car
import com.cenkeraydin.ttagmobil.data.retrofit.RetrofitInstance
import com.cenkeraydin.ttagmobil.data.retrofit.RetrofitInstance.api
import com.cenkeraydin.ttagmobil.util.DriverPrefsHelper
import com.cenkeraydin.ttagmobil.util.PreferencesHelper
import com.cenkeraydin.ttagmobil.util.UserPrefsHelper
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesHelper = PreferencesHelper(application.applicationContext)
    private val userPrefs = UserPrefsHelper(application.applicationContext)
    private val driverPrefs = DriverPrefsHelper(application.applicationContext)

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _driver = MutableStateFlow<Driver?>(null)
    val driver: StateFlow<Driver?> = _driver

    private val _selectedRole = MutableStateFlow(preferencesHelper.getSelectedRole())
    val selectedRole: StateFlow<String?> = _selectedRole


    init {
        loadProfile(application)
    }


    private fun loadProfile(application: Application) {
        val selectedRole = _selectedRole.value

        if (selectedRole == "Passenger") {
            _user.value = userPrefs.getUser()
        } else if (selectedRole == "Driver") {
            _driver.value = driverPrefs.getDriver(application)
        }
    }
    fun setUser(user: User) {
        _user.value = user
        userPrefs.saveUser(user)
    }

    fun setDriver(driver: Driver) {
        _driver.value = driver
        driverPrefs.saveDriver(driver)
    }

    fun saveCars(context: Context, cars: List<Car>) {
        val prefs = context.getSharedPreferences("cars", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(cars) // Bu, List<Car>'ı JSON dizi formatında kaydeder
        editor.putString("cars", json)
        editor.apply()
        Log.e("Cars", "Araba bilgileri kaydedildi: $cars")
    }


    fun logout(navHostController: NavHostController) {
        userPrefs.clear()
        driverPrefs.clear()
        _user.value = null
        _driver.value = null
        navHostController.navigate("login") {
            popUpTo("home") { inclusive = true }
        }
    }

    fun updateUserInfo(request: UpdateUserInfoRequest, context: Context) {
        viewModelScope.launch {
            try {
                val response = api.updateUserInfo(request)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Bilgiler güncellendi", Toast.LENGTH_SHORT).show()
                    val updatedUser = User(
                        id = user.value?.id ?: "",
                        email = request.email,
                        firstName = request.firstName,
                        lastName = request.lastName,
                        phoneNumber = request.phoneNumber,
                        userName = user.value?.userName,
                        pictureUrl = user.value?.pictureUrl
                    )
                    setUser(updatedUser)

                } else {
                    Log.e("UpdateError", "API hata: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("UpdateException", e.message ?: "Bilinmeyen hata")
            }
        }
    }

    fun updateDriverInfo(request: UpdateDriverInfoRequest, context: Context) {
        viewModelScope.launch {
            try {
                val response = api.updateDriverInfo(request)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Bilgiler güncellendi", Toast.LENGTH_SHORT).show()
                    val updatedDriver = Driver(
                        email = request.email,
                        firstName = request.firstName,
                        lastName = request.lastName,
                        identityNo = driver.value?.identityNo,
                        licenseUrl = driver.value?.licenseUrl,
                        experienceYears = request.experienceYear,
                        phoneNumber = request.phoneNumber,
                        pictureUrl = driver.value?.pictureUrl,
                        id = driver.value?.id,
                        userId = driver.value?.userId,
                        cars = driver.value?.cars ?: emptyList()
                    )
                    setDriver(updatedDriver)

                } else {
                    Log.e("UpdateError", "API hata: ${response.code() }- ${response.toString()}")
                }
            } catch (e: Exception) {
                Log.e("UpdateException", e.message ?: "Bilinmeyen hata")
            }
        }
    }

    fun deleteAccount(
        context: Context,
        navHostController: NavHostController,
        selectedRole: String?
    ) {
        viewModelScope.launch {
            try {
                val email = _user.value?.email
                if (email == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Email bulunamadı", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val response=if (selectedRole=="Passenger"){
                    api.deleteAccount(email)
                }
                else{
                    api.deleteDriverAccount(email)
                }

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, response.body()?.message ?: "Hesap silindi", Toast.LENGTH_SHORT).show()
                        navHostController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                } else {
                    val message = response.body()?.message ?: "Silme işlemi başarısız"
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                    Log.e("DeleteAccount", "Silinemedi: ${response.code()} ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("DeleteAccountException", e.message ?: "Hata oluştu")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun uploadProfilePicture(bitmap: Bitmap, context: Context, userId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = File(context.cacheDir, "profile_picture.jpg")
                file.outputStream().use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }

                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("Image", file.name, requestFile)
                val userIdPart = userId?.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = api.uploadProfilePicture(imagePart, userIdPart)

                if (response.isSuccessful) {
                    Log.d("Upload", "Başarılı")
                } else {
                    Log.e("Upload", "Hata: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Upload", "Exception: ${e.message}")
            }
        }
    }


    suspend fun uploadDriverLicense(
        bitmap: Bitmap,
        context: Context,
        userId: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            // Görseli geçici bir dosyaya kaydet
            val file = File(context.cacheDir, "license_image_${System.currentTimeMillis()}.jpg")
            file.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }

            // Görseli multipart olarak hazırla
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
            val userIdPart = userId.toRequestBody("text/plain".toMediaTypeOrNull())

            // API çağrısı
            val response = api.uploadDriverLicense(imagePart, userIdPart)

            if (response.isSuccessful) {
                val responseBody = response.body() // UploadResponse türünde olmalı
                Log.d("UploadLicense", "API Response: $responseBody")
                if (responseBody != null) {
                    // responseBody'yi kontrol et
                    if (responseBody.success && responseBody.imageUrl.isNotBlank()) {
                        val uploadedUrl = responseBody.imageUrl
                        Log.d("UploadLicense", "Başarılı, URL: $uploadedUrl")
                        onSuccess(uploadedUrl)
                    } else {
                        onError("Başarılı değil: ${responseBody.message}")
                    }
                } else {
                    onError("Yanıt boş.")
                }
            } else {
                val errorMessage = "Hata: ${response.message()} (Kod: ${response.code()})"
                Log.e("UploadLicense", errorMessage)
                onError(errorMessage)
            }
        } catch (e: Exception) {
            val errorMessage = "İstisna: ${e.localizedMessage}"
            Log.e("UploadLicense", errorMessage)
            onError(errorMessage)
        }
    }
}
