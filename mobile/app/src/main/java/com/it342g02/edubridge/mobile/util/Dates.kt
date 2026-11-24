package com.it342g02.edubridge.mobile.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/** Helpers for the ISO date strings the Spring API sends and expects. */
object Dates {
    private val dayFormat = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)
    private val dayTimeFormat = DateTimeFormatter.ofPattern("MMM d, h:mm a", Locale.US)
    private val headerFormat = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.US)

    fun parseDateTime(value: String?): LocalDateTime? = value?.let {
        try {
            LocalDateTime.parse(it.removeSuffix("Z"))
        } catch (_: Exception) {
            parseDate(it)?.atStartOfDay()
        }
    }

    fun parseDate(value: String?): LocalDate? = value?.let {
        try {
            LocalDate.parse(it.take(10))
        } catch (_: Exception) {
            null
        }
    }

    /** "2026-11-24T09:00:00" -> "Nov 24, 9:00 AM" */
    fun formatDateTime(value: String?): String = parseDateTime(value)?.format(dayTimeFormat) ?: "—"

    /** "2026-11-24" or a date-time -> "Nov 24, 2026" */
    fun formatDate(value: String?): String = parseDate(value)?.format(dayFormat) ?: "—"

    /** The uppercase date eyebrow on the web Dashboard, e.g. "TUESDAY, NOVEMBER 24, 2026" */
    fun todayEyebrow(): String = LocalDate.now().format(headerFormat).uppercase(Locale.US)

    /** Same rule as the web Dashboard's getGreeting(). */
    fun greeting(now: LocalTime = LocalTime.now()): String = when {
        now.hour < 12 -> "Good morning"
        now.hour < 18 -> "Good afternoon"
        else -> "Good evening"
    }

    fun nowIso(): String = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString()

    fun todayIso(): String = LocalDate.now().toString()

    /** Due date entered as yyyy-MM-dd -> end of that day, the format LocalDateTime expects. */
    fun endOfDayIso(date: LocalDate): String = date.atTime(23, 59).toString()

    /** "Due in 3 days", "Due today", "Overdue by 2 days" */
    fun dueLabel(value: String?): String {
        val due = parseDate(value) ?: return "No due date"
        val days = ChronoUnit.DAYS.between(LocalDate.now(), due)
        return when {
            days == 0L -> "Due today"
            days == 1L -> "Due tomorrow"
            days > 1 -> "Due in $days days"
            days == -1L -> "Overdue by 1 day"
            else -> "Overdue by ${-days} days"
        }
    }

    fun isPast(value: String?): Boolean =
        parseDate(value)?.isBefore(LocalDate.now()) ?: false
}
