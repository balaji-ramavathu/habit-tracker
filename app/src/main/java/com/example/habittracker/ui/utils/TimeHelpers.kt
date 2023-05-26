package com.example.habittracker.ui.utils

import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar


fun getMonthNameShort(monthNum: Int?): String {
    return when (monthNum) {
        1 -> "Jan"
        2 -> "Feb"
        3 -> "Mar"
        4 -> "Apr"
        5 -> "May"
        6 -> "Jun"
        7 -> "Jul"
        8 -> "Aug"
        9 -> "Sep"
        10 -> "Oct"
        11 -> "Nov"
        12 -> "Dec"
        else -> ""
    }
}


fun getDatesAndDaysForMonth(year: Int, month: Int): List<Pair<Int, String>> {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val datesAndDays = mutableListOf<Pair<Int, String>>()
    for (day in 1..daysInMonth) {
        calendar.set(year, month - 1, day)
        val dayAbbreviation = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "S"
            Calendar.MONDAY -> "M"
            Calendar.TUESDAY -> "T"
            Calendar.WEDNESDAY -> "W"
            Calendar.THURSDAY -> "T"
            Calendar.FRIDAY -> "F"
            Calendar.SATURDAY -> "S"
            else -> throw IllegalArgumentException("Invalid day of week")
        }
        datesAndDays.add(Pair(day, dayAbbreviation))
    }
    return datesAndDays
}
fun getFirstAndLastDayOfMonth(year: Int, month: Int): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, 1) // Month is zero-based in Calendar, so subtract 1

    val firstDayInMillis = calendar.timeInMillis

    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    calendar.set(year, month - 1, daysInMonth) // Set to the last day of the month

    val lastDayInMillis = calendar.timeInMillis

    return Pair(firstDayInMillis, lastDayInMillis)
}

fun getDateMillis(year: Int, month: Int, date: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.clear()
    calendar.set(year, month - 1, date)
    return calendar.timeInMillis
}

fun getDurationLeftFromNow(desiredTime: LocalTime): Duration {
    val now = LocalTime.now(ZoneId.systemDefault())
    val desiredDateTime = now.withHour(desiredTime.hour).withMinute(desiredTime.minute)
    val nextDateTime = if (desiredDateTime <= now) {
        desiredDateTime.plusHours(24)
    }
    else {
        desiredDateTime
    }
    return Duration.between(now, nextDateTime)

}
