package com.cenkeraydin.ttagmobil.data.model.car

data class CarCreateRequest(
    val driverId: String,
    val carBrand: String,
    val carModel: String,
    val passengerCapacity: Int,
    val luggageCapacity: Int,
    val price: Int
)