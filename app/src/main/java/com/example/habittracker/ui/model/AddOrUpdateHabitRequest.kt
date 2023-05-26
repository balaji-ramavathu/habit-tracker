package com.example.habittracker.ui.model

data class AddOrUpdateHabitRequest (
    val id: Int? = null,
    val name: String = "",
    val color: String? = null,
    val repeatType: UiRepeatType? = UiRepeatType.DAILY,
    val repeatValue: Int? = null,
    val reminderTime: String? = null,
    val delete: Boolean = false
)

enum class UiRepeatType(val displayName: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly")
}

