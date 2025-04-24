package com.cenkeraydin.ttagmobil.ui.profile

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.cenkeraydin.ttagmobil.data.model.UpdateUserInfoRequest
import com.cenkeraydin.ttagmobil.data.model.User
import com.cenkeraydin.ttagmobil.data.retrofit.RetrofitInstance
import com.cenkeraydin.ttagmobil.ui.logins.LoginViewModel
import com.cenkeraydin.ttagmobil.util.UserPrefsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val userPrefs = UserPrefsHelper(application.applicationContext)
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        loadUser()
    }

    private fun loadUser() {
        _user.value = userPrefs.getUser()
    }

    fun setUser(user: User) {
        _user.value = user
        userPrefs.saveUser(user)
    }

    fun logout(navHostController: NavHostController) {
        userPrefs.clear()
        _user.value = null
        navHostController.navigate("login") {
            popUpTo("home") { inclusive = true }
        }
    }

    fun updateUserInfo(request: UpdateUserInfoRequest, context: Context) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.updateUserInfo(request)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Bilgiler güncellendi", Toast.LENGTH_SHORT).show()
                    val updatedUser = User(
                        email = request.email,
                        firstName = request.firstName,
                        lastName = request.lastName,
                        phoneNumber = request.phoneNumber,
                        userName = user.value?.userName
                    )
                    setUser(updatedUser) // bu satır ekrandaki veriyi otomatik günceller

                } else {
                    Log.e("UpdateError", "API hata: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("UpdateException", e.message ?: "Bilinmeyen hata")
            }
        }
    }

    fun deleteAccount(
        context: Context,
        navHostController: NavHostController
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

                val response = RetrofitInstance.api.deleteAccount(email)

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, response.body()?.message ?: "Hesap silindi", Toast.LENGTH_SHORT).show()
                        navHostController.navigate("login") {
                            popUpTo(0) { inclusive = true } // Tüm stack temizlenir
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


}
