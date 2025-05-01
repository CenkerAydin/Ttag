package com.cenkeraydin.ttagmobil.data.model.auth

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val userName: String,
    val phoneNumber: String,
    val password: String,
    val confirmPassword: String
)
