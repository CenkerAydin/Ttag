package com.cenkeraydin.ttagmobil.ui.logins

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.cenkeraydin.ttagmobil.data.model.ForgotPasswordRequest
import com.cenkeraydin.ttagmobil.data.model.LoginRequest
import com.cenkeraydin.ttagmobil.data.model.ResetPasswordRequest
import com.cenkeraydin.ttagmobil.data.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    var loginState by mutableStateOf<String?>(null)
        private set

    var showResetDialog by mutableStateOf(false)
    var resetEmail by mutableStateOf("")
    var resetToken by mutableStateOf("")


    fun loginUser(request: LoginRequest, navController: NavController, context: Context) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.loginUser(request)
                Log.e("LoginResponse", response.toString())

                if (response.isSuccessful) {
                    val token = response.body()?.jwToken

                    if (!token.isNullOrBlank()) {
                        // Token'ı SharedPreferences'e kaydet
                        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                        prefs.edit().putString("jwt_token", token).apply()

                        Log.d("JWT Token", token)
                        loginState = "Tebrikler! Başarıyla giriş yaptınız"

                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        loginState = "Token alınamadı!"
                        Log.e("LoginError", "jwToken null veya boş")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginError", "API hata: ${response.code()} - $errorBody")
                    loginState = "error: $errorBody"
                }
            } catch (e: Exception) {
                Log.e("LoginException", e.message ?: "Unknown exception")
                loginState = "failure: ${e.message}"
            }
        }
    }

    fun logoutUser(context: Context, navController: NavController) {
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
    }



    fun clearLoginState() {
        loginState = null
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.forgotPassword(ForgotPasswordRequest(email))
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
        confirmPassword: String
    ) {
        viewModelScope.launch {
            try {
                val request = ResetPasswordRequest(
                    email = email,
                    token = token,
                    password = password,
                    confirmPassword = confirmPassword
                )
                val response = RetrofitInstance.api.resetPassword(request)
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
