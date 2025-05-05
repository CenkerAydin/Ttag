package com.cenkeraydin.ttagmobil.data.model.account

data class UpdateDriverInfoRequest(
    val email: String?,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val password: String,
    val licenseUrl: String,
    val experienceYear: Int
)
