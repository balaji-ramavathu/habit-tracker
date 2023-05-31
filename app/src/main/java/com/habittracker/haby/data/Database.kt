package com.habittracker.haby.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.habittracker.haby.data.dao.HabitDao
import com.habittracker.haby.data.dao.HabitEntryDao
import com.habittracker.haby.data.dao.HabitStreakDao
import com.habittracker.haby.data.entity.Habit
import com.habittracker.haby.data.entity.HabitEntry
import com.habittracker.haby.data.entity.HabitRepeatTypeConverter
import com.habittracker.haby.data.entity.HabitStreak
import com.habittracker.haby.data.entity.HabitTypeConverter
import com.habittracker.haby.data.Database as HabitDatabase


@Database(entities = [Habit::class, HabitEntry::class, HabitStreak::class], version = 2, exportSchema = false)
@TypeConverters(HabitTypeConverter::class, HabitRepeatTypeConverter::class)
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