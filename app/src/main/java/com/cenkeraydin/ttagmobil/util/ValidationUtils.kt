package com.cenkeraydin.ttagmobil.util

import java.text.SimpleDateFormat
import java.util.Locale

fun isValidDate(dateStr: String): Boolean {
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.isLenient = false
        sdf.parse(dateStr)
        true
    } catch (e: Exception) {
        false
    }
}

fun isValidHour(hourStr: String): Boolean {
    return try {
        val trimmed = hourStr.trim()
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.isLenient = false
        sdf.parse(trimmed) != null
    } catch (e: Exception) {
        false
    }
}

fun isValidPersonCount(input: String): Boolean {
    return input.trim().toIntOrNull()?.let { it > 0 } == true
}

fun isPasswordValid(password: String): Boolean {
    // Şifre en az bir büyük harf, bir küçük harf, bir rakam ve bir özel karakter içermeli
    val passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9])[A-Za-z\\d[^A-Za-z0-9]]{6,}\$"
    return password.matches(passwordPattern.toRegex())
}
