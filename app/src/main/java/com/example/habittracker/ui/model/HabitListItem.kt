package com.example.habittracker.ui.model

data class HabitListItem(
    val date: Int,
    val day: String,
    val month: Int,
    val monthName: String,
    val year: Int,
    val habitEntries: Map<Int, Boolean>
)