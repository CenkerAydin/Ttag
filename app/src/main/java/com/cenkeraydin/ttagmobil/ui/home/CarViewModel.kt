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
}
