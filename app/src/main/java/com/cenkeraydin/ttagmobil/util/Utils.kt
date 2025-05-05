package com.cenkeraydin.ttagmobil.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun toIsoDateTime(date: String?, hour: String?): String {
    return "${date}T${hour}:00"
}


fun formatReservationDate(dateString: String): Pair<String, String> {
    // Tarih string'ini Date objesine çeviriyoruz
    val dateFormatInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()) // Tarih formatı örneği: 2025-05-05T14:30:00
    val date: Date = try {
        dateFormatInput.parse(dateString) ?: throw IllegalArgumentException("Invalid date format")
    } catch (e: Exception) {
        throw IllegalArgumentException("Cannot parse the date string")
    }

    // Tarih formatı: "dd MMM yyyy"
    val dateFormatOutput = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val formattedDate = dateFormatOutput.format(date)

    // Saat formatı: "HH:mm"
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val formattedTime = timeFormat.format(date)

    return Pair(formattedDate, formattedTime)
}
