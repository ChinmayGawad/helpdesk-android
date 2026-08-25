package com.helpdesk.app.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateTimeUtils {

    private val isoFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") },
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") },
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US),
        SimpleDateFormat("yyyy-MM-dd", Locale.US)
    )

    private val displayFormat = SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault())
    private val shortDateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    fun parseIsoDate(isoString: String?): Date? {
        if (isoString.isNullOrBlank()) return null
        for (format in isoFormats) {
            try {
                return format.parse(isoString)
            } catch (_: Exception) {
            }
        }
        return null
    }

    fun formatDisplayDate(isoString: String?): String {
        val date = parseIsoDate(isoString) ?: return isoString ?: "Unknown"
        return displayFormat.format(date)
    }

    fun formatShortDate(isoString: String?): String {
        val date = parseIsoDate(isoString) ?: return isoString ?: ""
        return shortDateFormat.format(date)
    }

    fun formatRelativeTime(isoString: String?): String {
        val date = parseIsoDate(isoString) ?: return isoString ?: ""
        val now = System.currentTimeMillis()
        val diff = now - date.time

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days < 7 -> "${days}d ago"
            else -> shortDateFormat.format(date)
        }
    }
}
