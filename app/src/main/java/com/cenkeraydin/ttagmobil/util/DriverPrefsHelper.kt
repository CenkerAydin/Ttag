package com.cenkeraydin.ttagmobil.util

import com.cenkeraydin.ttagmobil.data.model.account.Driver
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.cenkeraydin.ttagmobil.data.model.car.Car
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream

class DriverPrefsHelper(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("driver_prefs", Context.MODE_PRIVATE)

    fun saveDriver(driver: Driver) {
        prefs.edit().apply {
            putString("id", driver.id)
            putString("userId", driver.userId)
            putString("cars", driver.cars.joinToString(",")) // opsiyonel
            putString("identityNo", driver.identityNo)
            putString("firstName", driver.firstName)
            putString("lastName", driver.lastName)
            putString("email", driver.email)
            putString("phoneNumber", driver.phoneNumber)
            putString("licenseUrl", driver.licenseUrl)
            putInt("experienceYear", driver.experienceYear ?: 0)
            putString("profile_image", driver.pictureUrl)
            val gson = Gson()
            val carsJson = gson.toJson(driver.cars)
            prefs.edit().putString("cars", carsJson).apply()
            apply()
        }
    }

    fun getDriver(context: Context): Driver? {
        val identityNo = prefs.getString("identityNo", null) ?: return null
        val firstName = prefs.getString("firstName", null) ?: return null
        val lastName = prefs.getString("lastName", null) ?: return null
        val email = prefs.getString("email", null) ?: return null
        val phoneNumber = prefs.getString("phoneNumber", null)
        val licenseUrl = prefs.getString("licenseUrl", null)
        val experienceYear = prefs.getInt("experienceYear", 0)
        val pictureUrl= prefs.getString("profile_image", null)
        val id = prefs.getString("id", null)
        val userId = prefs.getString("userId", null)
        return Driver(
            identityNo = identityNo,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phoneNumber = phoneNumber,
            licenseUrl = licenseUrl,
            experienceYear = experienceYear,
            pictureUrl = pictureUrl,
            id = id,
            userId = userId,
            cars = getCars(context)
        )

    }

    private fun getCars(context: Context): List<Car> {
        val prefs = context.getSharedPreferences("cars", Context.MODE_PRIVATE)
        val gson = Gson()
        val carListType = object : TypeToken<List<Car>>() {}.type
        val carsString = prefs.getString("cars", null)
        Log.e("DriverPrefsHelper1", "Araba bilgileri: $carsString")
        return carsString?.let {
            try {
                gson.fromJson(it, carListType)
            } catch (e: JsonSyntaxException) {
                Log.e("DriverPrefsHelper", "JSON parse hatası: ${e.localizedMessage}")
                emptyList()
            }
        } ?: emptyList()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun encodeBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val bytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }

    fun saveProfileImage(base64Image: String) {
        prefs.edit().putString("profile_image", base64Image).apply()
    }
    fun saveLicenseImage(base64: String) {
        prefs.edit().putString("licenseUrl", base64).apply()
    }

    fun getProfileImage(): Bitmap? {
        val base64 = prefs.getString("profile_image", null) ?: return null
        return decodeBase64ToBitmap(base64)
    }

    fun getDriverId(): String? {
        return prefs.getString("id", null)
    }

    fun getDriverName(): String? {
        val firstName = prefs.getString("firstName", null)
        val lastName = prefs.getString("lastName", null)
        return if (firstName != null && lastName != null) {
            "$firstName $lastName"
        } else {
            null
        }
    }
}
