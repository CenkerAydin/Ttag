package com.cenkeraydin.ttagmobil.ui.logins

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cenkeraydin.ttagmobil.data.model.auth.RegisterRequest
import com.cenkeraydin.ttagmobil.data.retrofit.RetrofitInstance
import com.cenkeraydin.ttagmobil.util.PreferencesHelper
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    var registrationState by mutableStateOf<String?>(null)

    fun registerUser(
        request: RegisterRequest,
        selectedRole: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = if (selectedRole == "Passenger") {
                    RetrofitInstance.api.registerUser(request)
                } else {
                    RetrofitInstance.api.registerDriver(request)
                }

                Log.d("RegisterResponse", response.toString())

                val preferencesHelper = PreferencesHelper(context)
                preferencesHelper.saveSelectedRole(selectedRole)
                if (response.isSuccessful) {
                    val message = response.body()?.string()
                    Log.d("Register", "Kayıt mesajı: $message")
                    registrationState = "success"
                    onSuccess()
                } else {
                    // Burada 500 dönse bile başarılı kabul etmek istiyorsan:
                    val errorBody = response.errorBody()?.string()
                    Log.e("RegisterError", "Hata: ${response.code()} - $errorBody")

                    if (response.code() == 500) {
                        // Kayıt aslında olmuş olabilir, bu yüzden başarılı gibi davran
                        registrationState = "success_with_warning"
                        onSuccess()
                    } else {
                        registrationState = "error: $errorBody"
                        onError(errorBody ?: "Bilinmeyen hata")
                    }
                }
            } catch (e: Exception) {
                Log.e("RegisterException", e.message ?: "Unknown exception")
                registrationState = "failure: ${e.message}"
                onError(e.message ?: "İstisna oluştu")
            }
        }
    }


    fun confirmEmail(
        email: String,
        code: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.confirmEmail(email, code)
                if (response.isSuccessful) {
                    Log.d("EmailConfirm", "Doğrulama başarılı")
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("EmailConfirmError", "Kod hatalı: $errorBody")
                    onError(errorBody ?: "Kod hatalı")
                }
            } catch (e: Exception) {
                Log.e("EmailConfirmException", e.message ?: "Unknown exception")
                onError(e.message ?: "İstisna oluştu")
            }
        }
    }
}