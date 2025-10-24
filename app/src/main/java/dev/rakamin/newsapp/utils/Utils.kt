package dev.rakamin.newsapp.utils

import java.text.SimpleDateFormat
import java.util.Locale

object Utils {

    fun formatNewsDate(oldDate: String?): String {
        if (oldDate == null) return ""

        val inputFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            Locale.getDefault()
        )

        val indonesiaLocale = Locale.Builder().setLanguage("id").setRegion("ID").build()
        val outputFormat = SimpleDateFormat("dd MMM, yyyy", indonesiaLocale)

        return try {
            val date = inputFormat.parse(oldDate)
            if (date != null) {
                outputFormat.format(date)
            } else {
                oldDate
            }
        } catch (e: Exception) {
            e.printStackTrace()
            oldDate
        }
    }
}