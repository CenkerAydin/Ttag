package com.cenkeraydin.ttagmobil.util

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.cenkeraydin.ttagmobil.data.model.User
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
            apply()
        }
    }

    fun getUser(): User? {
        val firstName = prefs.getString("name", null) ?: return null
        val lastName = prefs.getString("surname", null) ?: return null
        val phoneNumber = prefs.getString("phone", null) ?: return null
        val email = prefs.getString("email", null) ?: return null
        val userName= prefs.getString("userName", null) ?: return null
        return User(firstName, lastName, email, phoneNumber,userName)
    }

    fun clear() {
        prefs.edit().apply {
            remove("name")
            remove("surname")
            remove("phone")
            remove("email")
            remove("userName")
            apply()
        }    }

    fun encodeBitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun decodeBase64ToBitmap(base64Str: String): Bitmap {
        val bytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    fun saveProfileImage(base64Image: String) {
        prefs.edit().putString("profile_image", base64Image).apply()
    }

    fun getProfileImage(): Bitmap? {
        val base64 = prefs.getString("profile_image", null) ?: return null
        return decodeBase64ToBitmap(base64)
    }


}
