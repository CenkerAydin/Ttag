package com.cenkeraydin.ttagmobil.data.model.account

import com.cenkeraydin.ttagmobil.data.model.car.Car

data class Driver(
    val id: String?,
    val userId:String?,
    val cars: List<Car>,
    val identityNo: String?,
    val licenseUrl: String?,
    val experienceYear: Int?,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val phoneNumber: String?,
    val pictureUrl: String?
)