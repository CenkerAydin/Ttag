package com.cenkeraydin.ttagmobil.data.model

data class CarResponse(
    val succeeded: Boolean,
    val message: String?,
    val errors: List<String>?,
    val data: List<Car>
)

data class Car(
    val id: String,
    val driverId: String,
    val carBrand: String,
    val carModel: String,
    val passengerCapacity: Int,
    val luggageCapacity: Int,
    val price: Int
)
