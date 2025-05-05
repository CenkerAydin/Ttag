package com.cenkeraydin.ttagmobil.data.model.car

data class Car(
    val id: String,
    val driverId: String,
    val carBrand: String,
    val carModel: String,
    val passengerCapacity: Int,
    val luggageCapacity: Int,
    val price: Int,
    val imageUrls: List<String>
)