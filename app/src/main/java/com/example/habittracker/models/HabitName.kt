package com.example.habittracker.models

data class HabitName(
    val name: String,
    val indicator: String,
    var done: Boolean = false
)
