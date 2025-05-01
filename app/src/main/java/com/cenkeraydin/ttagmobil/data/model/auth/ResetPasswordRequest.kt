package com.cenkeraydin.ttagmobil.data.model.auth

data class ResetPasswordRequest(
    val email: String,
    val token: String,
    val password: String,
    val confirmPassword: String
)