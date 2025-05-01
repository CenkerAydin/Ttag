package com.cenkeraydin.ttagmobil.data.model.auth

data class AuthBody(
    val id: String,
    val userName: String,
    val email: String,
    val roles: List<String>,
    val isVerified: Boolean,
    val jwToken: String
)
