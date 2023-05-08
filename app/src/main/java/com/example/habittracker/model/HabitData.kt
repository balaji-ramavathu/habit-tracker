package com.example.habittracker.model

import java.util.Date

data class HabitData(
    val date: Date,
    val day: Char,
    val habits: List<HabitName>
)