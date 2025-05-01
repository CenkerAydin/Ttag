package com.cenkeraydin.ttagmobil.util

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val SELECTED_ROLE_KEY = "selected_role"
    }

    // selectedRole verisini al
    fun getSelectedRole(): String? {
        return sharedPreferences.getString(SELECTED_ROLE_KEY, "")
    }

    // selectedRole verisini kaydet
    fun saveSelectedRole(role: String) {
        sharedPreferences.edit().putString(SELECTED_ROLE_KEY, role).apply()
    }
}
