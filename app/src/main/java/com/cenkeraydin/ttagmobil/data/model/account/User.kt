package com.cenkeraydin.ttagmobil.data.model.account

data class User(
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String?,
    val userName: String?,
    val pictureUrl: String?
)
