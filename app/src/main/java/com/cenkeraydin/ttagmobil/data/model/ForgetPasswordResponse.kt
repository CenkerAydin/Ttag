package com.cenkeraydin.ttagmobil.data.model

data class ForgotPasswordResponse(
    val to: String,
    val subject: String,
    val body: String,
    val from: String?
)
