package com.cenkeraydin.ttagmobil.data.model.auth

data class ForgotPasswordResponse(
    val to: String,
    val subject: String,
    val body: String,
    val from: String?
)
