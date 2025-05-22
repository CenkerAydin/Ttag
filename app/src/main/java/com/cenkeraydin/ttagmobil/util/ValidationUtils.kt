package com.cenkeraydin.ttagmobil.util

import android.util.Log
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun isValidDate(date: String): Boolean {
    val regex = Regex("""\d{4}-\d{2}-\d{2}""") // yyyy-MM-dd formatı
    return if (date.matches(regex)) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            true
        } catch (e: DateTimeParseException) {
            Log.e("DateValidation", "Invalid date: $date, error: ${e.message}")
            false
        }
    } else {
        false
    }
}

fun isValidHour(hour: String): Boolean {
    val regex = Regex("""\d{2}:\d{2}""") // HH:mm formatı
    if (!hour.matches(regex)) return false

    val (hh, mm) = hour.split(":").map { it.toIntOrNull() ?: return false }

    return hh in 0..23 && mm in 0..59
}

fun isValidPersonCount(count: String): Boolean {
    return count.toIntOrNull()?.let { it > 0 } ?: false
}

fun createDateTime(date: String, time: String): LocalDateTime? {
    return try {
        val dateTimeString = "$date $time"
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        LocalDateTime.parse(dateTimeString, formatter)
    } catch (e: DateTimeParseException) {
        Log.e("DateTime", "Parsing failed: ${e.message}")
        null
    }
}

fun isPasswordValid(password: String): Boolean {
    // Şifre en az bir büyük harf, bir küçük harf, bir rakam ve bir özel karakter içermeli
    val passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9])[A-Za-z\\d[^A-Za-z0-9]]{6,}\$"
    return password.matches(passwordPattern.toRegex())
}

