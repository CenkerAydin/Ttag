package com.cenkeraydin.ttagmobil.data.model.account

data class UpdateDriverInfoRequest(
    val email: String?,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val password: String,
    val licenseUrl: String, // BU ALAN EKSİKTI
    val experienceYear: Int// Driver-specific field
)
