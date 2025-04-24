package com.cenkeraydin.ttagmobil.data.model

data class DeleteAccountResponse(
    val succeeded: Boolean,
    val message: String,
    val errors: List<String>?,
    val data: String?
)