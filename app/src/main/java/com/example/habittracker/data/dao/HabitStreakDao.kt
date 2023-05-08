package com.example.habittracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.habittracker.data.entity.HabitStreak

@Dao
interface HabitStreakDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitStreak(habit: HabitStreak)

    @Query("SELECT * FROM habit_streaks")
    fun getAll(): List<HabitStreak>

    @Query("SELECT * FROM habit_streaks WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<HabitStreak>

    @Query("SELECT * FROM habit_streaks WHERE habit_id IN (:habitIds)")
    fun loadAllByHabitIds(habitIds: IntArray): List<HabitStreak>

    @Insert
    fun insertAll(vararg habitStreaks: List<HabitStreak>)

    @Delete
    fun delete(habitStreak: HabitStreak)
}