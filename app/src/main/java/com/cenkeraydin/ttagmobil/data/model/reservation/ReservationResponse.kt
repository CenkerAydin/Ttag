package com.cenkeraydin.ttagmobil.data.model.reservation

data class ReservationResponse(
    val id: String,
    val driverId: String,
    val driverFirstName: String,
    val driverLastName: String,
    val driverPictureUrl: String,
    val userId: String,
    val startDateTime: String,
    val endDateTime: String,
    val fromWhere: String,
    val toWhere: String,
    val price: Double,
    val status: Int
)
