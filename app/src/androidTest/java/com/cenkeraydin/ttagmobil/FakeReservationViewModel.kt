package com.cenkeraydin.ttagmobil

import android.app.Application
import com.cenkeraydin.ttagmobil.data.model.account.AvailableDriver
import com.cenkeraydin.ttagmobil.ui.reservation.ReservationViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeReservationViewModel(app: Application) : ReservationViewModel(app) {

    fun fetchAvailableDriversNo_Op(s: String, e: String) { /* no-op */ }

     private val _driversTest = MutableStateFlow<List<AvailableDriver>>(emptyList())
     val driversTest: StateFlow<List<AvailableDriver>> = _driversTest
}
