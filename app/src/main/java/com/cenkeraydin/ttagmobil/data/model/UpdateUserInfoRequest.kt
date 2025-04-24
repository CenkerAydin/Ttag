package com.cenkeraydin.ttagmobil.data.model

data class UpdateUserInfoRequest(
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val password: String?,
)
