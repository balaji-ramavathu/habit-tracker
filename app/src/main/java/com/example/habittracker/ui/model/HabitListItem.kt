package com.example.habittracker.ui.model

data class HabitListItem(
    val date: Int,
    val day: String,
    val month: Int,
    val year: Int,
    val habitEntries: Map<Int, HabitEntryState>
)

enum class HabitEntryState {
    COMPLETED,      // tick
    APPLICABLE_AND_INCOMPLETE,  // cross
    NOT_APPLICABLE      // outlined tick
}