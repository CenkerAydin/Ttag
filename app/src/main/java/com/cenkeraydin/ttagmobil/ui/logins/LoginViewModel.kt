package com.cenkeraydin.ttagmobil.ui.logins

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.cenkeraydin.ttagmobil.data.model.account.Driver
import com.cenkeraydin.ttagmobil.data.model.auth.ForgotPasswordRequest
import com.cenkeraydin.ttagmobil.data.model.auth.LoginRequest
import com.cenkeraydin.ttagmobil.data.model.auth.ResetPasswordRequest
import com.cenkeraydin.ttagmobil.data.model.account.User
import com.cenkeraydin.ttagmobil.data.retrofit.RetrofitInstance
import com.cenkeraydin.ttagmobil.ui.profile.ProfileViewModel
import com.cenkeraydin.ttagmobil.util.PreferencesHelper
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    var loginState by mutableStateOf<String?>(null)
        private set

    var showResetDialog by mutableStateOf(false)
    var resetEmail by mutableStateOf("")
    var resetToken by mutableStateOf("")


    fun loginUser(
        request: LoginRequest,
        navController: NavController,
        context: Context,
        profileViewModel: ProfileViewModel,
        selectedRole: String
    ) {
        viewModelScope.launch {
            try {
                val response = if (selectedRole == "Passenger") {
                    RetrofitInstance.api.loginUser(request)  // 'loginDriver' API çağrısı
                } else {
                    RetrofitInstance.api.loginDriver(request)  // 'loginPassenger' API çağrısı
                }
                Log.e("LoginResponse", response.toString())

                val preferencesHelper = PreferencesHelper(context)
                preferencesHelper.saveSelectedRole(selectedRole)

                if (response.isSuccessful) {
                    val authBody = response.body()
                    if (authBody != null && authBody.jwToken.isNotBlank()) {
                        // 🔐 Token'ı sakla
                        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                        prefs.edit().putString("jwt_token", authBody.jwToken).apply()
                        Log.d("Login", "Token başarıyla alındı: ${authBody.jwToken}")
                        val userInfoResponse = RetrofitInstance.api.getUserInfo(request.email)
                        val driverInfoResponse = RetrofitInstance.api.getDriverInfo(request.email)
                        Log.e("UserInfoResponse", userInfoResponse.toString())
                        Log.e("DriverInfoResponse", driverInfoResponse.toString())
                        if (driverInfoResponse.isSuccessful) {
                            val driverData = driverInfoResponse.body()?.data  // Burada data'ya erişim yapıyoruz
                            Log.d("Driver Data", driverData.toString()) // Driver Data'yı logla
                            if (driverData != null) {
                                val driver = Driver(
                                    firstName = driverData.firstName ?: "",
                                    lastName = driverData.lastName ?: "",
                                    email = driverData.email ?: "",
                                    phoneNumber = driverData.phoneNumber ?: "",
                                    identityNo = driverData.identityNo ?: "",
                                    licenseUrl = driverData.licenseUrl ?: "",
                                    experienceYear = driverData.experienceYear ?: 0,
                                    pictureUrl = driverData.pictureUrl ?: "",
                                    id = driverData.id ?: "",
                                    userId = driverData.userId ?: "",
                                    cars = driverData.cars ?: emptyList()
                                )
                                val driverCars = driverData.cars
                                profileViewModel.saveCars(context,driverCars)
                                profileViewModel.setDriver(driver)
                            }
                        } else {
                            Log.e("LoginDriverError", "Kullanıcı bilgileri alınamadı!")
                        }

                        if (userInfoResponse.isSuccessful) {
                            val userData = userInfoResponse.body()?.data  // Burada data'ya erişim yapıyoruz
                            Log.d("User Data", userData.toString()) // User Data'yı logla                            if (userData != null) {
                                val user = User(
                                    id = userData?.id ?: "",
                                    firstName = userData?.firstName ?: "",
                                    lastName = userData?.lastName ?: "",
                                    email = userData?.email ?: "",
                                    phoneNumber = userData?.phoneNumber ?: "",
                                    userName = userData?.userName ?: "",
                                    pictureUrl = userData?.pictureUrl ?: ""
                                )
                                profileViewModel.setUser(user)
                            }
                        else {
                            Log.e("LoginUserError", "Kullanıcı bilgileri alınamadı!")
                        }
                        loginState = "Giriş başarılı"
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        loginState = "Token alınamadı!"
                        Log.e("LoginErr", "authBody null veya jwToken boş")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginErr", "API hata: ${response.code()} - $errorBody")
                    loginState = "error: $errorBody"
                }
            } catch (e: Exception) {
                Log.e("LoginException", e.message ?: "Unknown exception")
                loginState = "failure: ${e.message}"
            }
        }
    }

    fun logoutUser(context: Context, navController: NavHostController, profileViewModel: ProfileViewModel) {
        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        prefs.edit().remove("jwt_token").apply()

        val token = prefs.getString("jwt_token", null)
        if (token == null) {
            Log.d("Logout", "Token başarıyla silindi!")
        } else {
            Log.d("Logout", "Token hala mevcut: $token")
        }

        navController.navigate("login") {
            popUpTo("home") { inclusive = true }
        }
        profileViewModel.logout(navController)


    }




    fun clearLoginState() {
        loginState = null
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.forgotPasswordAccount(ForgotPasswordRequest(email))
                if (response.isSuccessful) {
                    Log.e("ForgotPasswordResponse", response.toString())
                    val body = response.body()?.body
                    val tokenRegex = Regex("token is - (.+)")
                    val token = tokenRegex.find(body ?: "")?.groupValues?.get(1)
                    Log.e("ForgotPasswordResponse", body ?: "No body")
                    Log.e("ForgotPasswordToken", token ?: "No token")
                    if (!token.isNullOrBlank()) {
                        resetEmail = email
                        resetToken = token
                        showResetDialog = true
                    }
                }
            } catch (e: Exception) {
                Log.e("ForgotPasswordException", e.localizedMessage ?: "Hata")
            }
        }
    }

    fun resetPassword(
        email: String,
        token: String,
        password: String,
        confirmPassword: String,
        selectedRole: String
    ) {
        viewModelScope.launch {
            try {
                val request = ResetPasswordRequest(
                    email = email,
                    token = token,
                    password = password,
                    confirmPassword = confirmPassword
                )
                val response = if (selectedRole == "Passenger") {
                    RetrofitInstance.api.resetAccountPassword(request)
                } else {
                    RetrofitInstance.api.resetDriverPassword(request)
                }
                Log.e("ResetResponse", response.toString())
                if (response.isSuccessful) {
                    val message = response.body()?.string()
                    if (message != null) {
                        Log.d("ResetPassword", message) // Mesajı logluyoruz
                        loginState = "Şifre başarıyla sıfırlandı."
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ResetPasswordError", errorBody ?: "Bilinmeyen hata")
                    loginState = "Şifre sıfırlama sırasında bir hata oluştu."
                }
            } catch (e: Exception) {
                Log.e("ResetPasswordException", e.localizedMessage ?: "İstisna oluştu")
                loginState = "Şifre sıfırlama sırasında bir hata oluştu."
            }
        }
    }



}
