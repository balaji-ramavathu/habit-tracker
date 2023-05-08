package com.example.habittracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.habittracker.data.entity.HabitEntry

@Dao
interface HabitEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitEntry(habit: HabitEntry)

    @Query("SELECT * FROM habit_entries")
    fun getAll(): List<HabitEntry>

    @Query("SELECT * FROM habit_entries WHERE id IN (:habitIds)")
    fun loadAllByIds(habitIds: IntArray): List<HabitEntry>

    @Query("SELECT * FROM habit_entries WHERE habit_id IN (:habitIds)")
    fun loadAllByHabitIds(habitIds: IntArray): List<HabitEntry>

    @Insert
    fun insertAll(vararg habitEntries: HabitEntry)

    @Delete
    fun delete(habitEntry: HabitEntry)
}