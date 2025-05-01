package com.cenkeraydin.ttagmobil.util

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.cenkeraydin.ttagmobil.data.model.account.User
import java.io.ByteArrayOutputStream

class UserPrefsHelper(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        prefs.edit().apply {
            putString("name", user.firstName)
            putString("surname", user.lastName)
            putString("phone", user.phoneNumber)
            putString("email", user.email)
            putString("userName", user.userName)
            putString("id", user.id)
            putString("profile_image", user.pictureUrl)
            apply()
        }
    }

    fun getUser(): User? {
        val id= prefs.getString("id", null) ?: return null
        val firstName = prefs.getString("name", null) ?: return null
        val lastName = prefs.getString("surname", null) ?: return null
        val phoneNumber = prefs.getString("phone", null) ?: return null
        val email = prefs.getString("email", null) ?: return null
        val userName= prefs.getString("userName", null) ?: return null
        val pictureUrl= prefs.getString("profile_image", null) ?: return null
        Log.e("UserPrefsHelper", "User retrieved: ${User(id,firstName, lastName, email, phoneNumber,userName, pictureUrl )}")
        return User(id,firstName, lastName, email, phoneNumber,userName, pictureUrl )
    }

    fun clear() {
        prefs.edit().apply {
            remove("name")
            remove("surname")
            remove("phone")
            remove("email")
            remove("userName")
            remove("id")
            remove("profile_image")
            apply()
        }    }

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

    fun getProfileImage(): Bitmap? {
        val base64 = prefs.getString("profile_image", null) ?: return null
        return decodeBase64ToBitmap(base64)
    }

    fun getUserId(): String? {
        return prefs.getString("id", null)
    }


}
