package com.example.habittracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.habittracker.data.dao.HabitDao
import com.example.habittracker.data.dao.HabitEntryDao
import com.example.habittracker.data.dao.HabitStreakDao
import com.example.habittracker.data.entity.Habit
import com.example.habittracker.data.entity.HabitEntry
import com.example.habittracker.data.entity.HabitStreak
import com.example.habittracker.data.Database as HabitDatabase


@Database(entities = [Habit::class, HabitEntry::class, HabitStreak::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun habitEntryDao(): HabitEntryDao
    abstract fun habitStreakDao(): HabitStreakDao

    companion object {
        @Volatile
        private var instance: HabitDatabase? = null
        fun getInstance(context: Context): HabitDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java, "habit_database"
                ).build()
            }
    }

}