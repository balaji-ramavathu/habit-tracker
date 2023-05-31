package com.habittracker.haby.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "habits")
data class Habit(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "indicator")
    val indicator: String,

    @ColumnInfo(name = "color")
    val color: String,

    @ColumnInfo(name = "type")
    val type: HabitType = HabitType.BOOLEAN,

    @Embedded
    var repeatInfo: HabitRepeatInfo,

    @Embedded
    var reminderInfo: HabitReminderInfo? = null,

    @ColumnInfo(name = "priority")
    val priority: Int = 0,

    @ColumnInfo(name = "archived")
    val archived: Boolean = false,

    @ColumnInfo(name = "added_at")
    val addedAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)


enum class HabitType {
    BOOLEAN, NUMBER, TEXT
}

data class HabitRepeatInfo(
    @ColumnInfo(name = "repeat_type")
    var repeatType: HabitRepeatType = HabitRepeatType.DAILY,

    @ColumnInfo(name = "repeat_value")
    var repeatValue: Int = 1
)

enum class HabitRepeatType {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

data class HabitReminderInfo(
    @ColumnInfo(name = "reminder_time")
    val reminderTime: String
)

class HabitRepeatTypeConverter {
    @TypeConverter
    fun fromString(value: String): HabitRepeatType {
        return HabitRepeatType.valueOf(value)
    }

    @TypeConverter
    fun habitRepeatTypeToString(value: HabitRepeatType): String {
        return value.name
    }
}
class HabitTypeConverter {
    @TypeConverter
    fun fromString(value: String): HabitType {
        return HabitType.valueOf(value)
    }

    @TypeConverter
    fun habitTypeToString(value: HabitType): String {
        return value.name
    }
}
