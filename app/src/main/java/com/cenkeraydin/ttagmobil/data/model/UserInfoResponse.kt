package com.cenkeraydin.ttagmobil.data.model

data class UserInfoResponse(
    val succeeded: Boolean,
    val message: String?,
    val errors: List<String>?,
    val data: User
)