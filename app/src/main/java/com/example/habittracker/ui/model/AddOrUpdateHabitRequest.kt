package com.example.habittracker.ui.model

data class AddOrUpdateHabitRequest (
    val name: String = "",
    val color: String? = null,
    val repeatType: UiRepeatType? = UiRepeatType.DAILY,
    val repeatValue: Int? = null,
    val reminderTime: String? = null
)

enum class UiRepeatType(val displayName: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly"),
    CUSTOM("Custom")
}

