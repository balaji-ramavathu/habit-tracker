package com.example.habittracker.ui.utils

import java.util.Calendar
import java.util.Locale

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
        calendar.set(year, month, day)
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

fun getDaysDisplayNameMap() : Map<String, Int> {
    val calendar = Calendar.getInstance()
    return calendar.getDisplayNames(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())!!
}