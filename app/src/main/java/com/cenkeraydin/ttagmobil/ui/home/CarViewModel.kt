package com.cenkeraydin.ttagmobil.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cenkeraydin.ttagmobil.data.model.Car
import com.cenkeraydin.ttagmobil.data.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

class CarViewModel : ViewModel() {
    var cars by mutableStateOf<List<Car>>(emptyList())
        private set

    var errorMessage by mutableStateOf<String?>(null)

    fun fetchCars() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getCars()
                if (response.isSuccessful) {
                    cars = response.body()?.data ?: emptyList()
                } else {
                    errorMessage = response.errorBody()?.string()
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
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
}
