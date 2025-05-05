package com.cenkeraydin.ttagmobil.data.model.car

data class CarResponse(
    val succeeded: Boolean,
    val message: String?,
    val errors: List<String>?,
    val data: List<Car>
)

