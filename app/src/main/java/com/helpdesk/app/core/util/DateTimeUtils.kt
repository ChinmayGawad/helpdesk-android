/**
 * Date/time formatting utilities for displaying timestamps in user-facing strings
 * (relative time, short date, full display date).
 */
package com.helpdesk.app.core.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

object DateTimeUtils {

    private val displayFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("MMM d, yyyy · h:mm a", Locale.getDefault())
    private val shortDateFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())

    fun parseIsoInstant(isoString: String?): Instant? {
        if (isoString.isNullOrBlank()) return null
        val trimmed = isoString.trim()

        // 1. Try standard Instant parse (ISO-8601 UTC like 2026-08-29T12:00:00Z or 2026-08-29T12:00:00.000Z)
        try {
            return Instant.parse(trimmed)
        } catch (_: Exception) {
        }

        // 2. Try ZonedDateTime (with offset like +05:30)
        try {
            return ZonedDateTime.parse(trimmed).toInstant()
        } catch (_: Exception) {
        }

        // 3. Try LocalDateTime without timezone (assume UTC or system)
        try {
            return LocalDateTime.parse(trimmed).atZone(ZoneOffset.UTC).toInstant()
        } catch (_: Exception) {
        }

        // 4. Try LocalDate (e.g. "2026-08-29")
        try {
            return LocalDate.parse(trimmed).atStartOfDay(ZoneOffset.UTC).toInstant()
        } catch (_: Exception) {
        }

        return null
    }

    fun parseIsoDate(isoString: String?): Date? {
        val instant = parseIsoInstant(isoString) ?: return null
        return Date.from(instant)
    }

    fun formatDisplayDate(isoString: String?): String {
        val instant = parseIsoInstant(isoString) ?: return isoString ?: "Unknown"
        return displayFormatter.withZone(ZoneId.systemDefault()).format(instant)
    }

    fun formatShortDate(isoString: String?): String {
        val instant = parseIsoInstant(isoString) ?: return isoString ?: ""
        return shortDateFormatter.withZone(ZoneId.systemDefault()).format(instant)
    }

    fun formatRelativeTime(isoString: String?): String {
        val instant = parseIsoInstant(isoString) ?: return isoString ?: ""
        val now = Instant.now()
        val seconds = ChronoUnit.SECONDS.between(instant, now)

        if (seconds < 0) {
            return formatShortDate(isoString)
        }

        val minutes = ChronoUnit.MINUTES.between(instant, now)
        val hours = ChronoUnit.HOURS.between(instant, now)
        val days = ChronoUnit.DAYS.between(instant, now)

        return when {
            seconds < 60 -> "just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days < 7 -> "${days}d ago"
            else -> formatShortDate(isoString)
        }
    }
}
