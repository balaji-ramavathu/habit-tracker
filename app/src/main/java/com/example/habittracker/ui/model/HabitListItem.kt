package com.example.habittracker.ui.model

data class HabitListItem(
    val date: Int,
    val day: String,
    val month: String,
    val habitEntries: Map<Int, Boolean>
)