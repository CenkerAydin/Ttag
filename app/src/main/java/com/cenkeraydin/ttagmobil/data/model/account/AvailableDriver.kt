package com.cenkeraydin.ttagmobil.data.model.account

import com.cenkeraydin.ttagmobil.data.model.car.Car

data class AvailableDriver(
    val driverId: String,
    val firstName: String,
    val lastName: String,
    val pictureUrl: String,
    val experienceYears: Int,
    val cars: List<Car>
)