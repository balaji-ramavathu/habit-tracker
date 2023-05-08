package com.example.habittracker.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "indicator")
    val indicator: String,

    @ColumnInfo(name = "color")
    val color: String,

    @ColumnInfo(name = "type")
    val type: HabitType,

    @Embedded
    @ColumnInfo(name = "repeat_info")
    val repeatInfo: HabitRepeatInfo,

    @Embedded
    @ColumnInfo(name = "reminder_info")
    val reminderInfo: HabitReminderInfo,

    @ColumnInfo(name = "priority")
    val priority: Int = 0,

    @ColumnInfo(name = "archived")
    val archived: Boolean = false,

    @ColumnInfo(name = "added_at")
    val addedAt: Long
)


enum class HabitType {
    BOOLEAN, NUMBER, TEXT
}

data class HabitRepeatInfo(
    @ColumnInfo(name = "repeat_type")
    val repeatType: HabitRepeatType,

    @ColumnInfo(name = "repeat_value")
    val repeatValue: Int = 1
)

enum class HabitRepeatType {
    DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM
}

data class HabitReminderInfo(
    @ColumnInfo(name = "reminder_time")
    val reminderTime: Long,

    @ColumnInfo(name = "reminder_days")
    val reminderDays: List<Int>
)