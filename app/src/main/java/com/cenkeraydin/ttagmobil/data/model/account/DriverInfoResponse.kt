package com.cenkeraydin.ttagmobil.data.model.account

data class DriverInfoResponse(
    val succeeded: Boolean,
    val message: String?,
    val errors: List<String>?,
    val data: Driver
)