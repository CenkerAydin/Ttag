package com.cenkeraydin.ttagmobil.data.model.reservation

data class CreateReservationRequest(
    val driverId: String?,
    val userId: String?,
    val startDateTime: String?,
    val endDateTime: String?,
    val fromWhere: String?,
    val toWhere: String?,
    val price: Int?
)