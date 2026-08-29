package com.helpdesk.app

import com.helpdesk.app.core.util.DateTimeUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class DateTimeUtilsTest {

    @Test
    fun parseIsoInstant_validIsoUtc_returnsInstant() {
        val iso = "2026-08-29T12:00:00.000Z"
        val instant = DateTimeUtils.parseIsoInstant(iso)
        assertNotNull(instant)
    }

    @Test
    fun parseIsoInstant_nullOrBlank_returnsNull() {
        assertNull(DateTimeUtils.parseIsoInstant(null))
        assertNull(DateTimeUtils.parseIsoInstant(""))
        assertNull(DateTimeUtils.parseIsoInstant("   "))
    }

    @Test
    fun formatShortDate_validDate_formatsCorrectly() {
        val iso = "2026-08-29T12:00:00Z"
        val formatted = DateTimeUtils.formatShortDate(iso)
        assertTrue(formatted.contains("2026"))
        assertTrue(formatted.contains("Aug") || formatted.contains("8"))
    }

    @Test
    fun formatRelativeTime_justNow_returnsJustNow() {
        val nowIso = Instant.now().toString()
        val relative = DateTimeUtils.formatRelativeTime(nowIso)
        assertEquals("just now", relative)
    }

    @Test
    fun formatRelativeTime_pastDate_returnsExpectedUnits() {
        val pastTenMinutes = Instant.now().minusSeconds(600).toString()
        val relative = DateTimeUtils.formatRelativeTime(pastTenMinutes)
        assertEquals("10m ago", relative)
    }
}
