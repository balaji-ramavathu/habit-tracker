package com.habittracker.haby.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_streaks",
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habit_id"],
            onDelete = CASCADE
        )
    ]
)
data class HabitStreak(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "habit_id", index = true)
    val habitId: Int,

    val startDate: Long,

    val endDate: Long
)
