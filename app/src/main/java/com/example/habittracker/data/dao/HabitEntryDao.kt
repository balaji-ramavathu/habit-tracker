package com.example.habittracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.habittracker.data.entity.HabitEntry

@Dao
interface HabitEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHabitEntry(habitEntry: HabitEntry)

    @Query("SELECT * FROM habit_entries")
    fun getAll(): List<HabitEntry>

    @Query("SELECT * FROM habit_entries WHERE id IN (:habitIds)")
    fun loadAllByIds(habitIds: IntArray): List<HabitEntry>

    @Query("SELECT * FROM habit_entries WHERE habit_id IN (:habitIds)")
    fun loadAllByHabitIds(habitIds: IntArray): List<HabitEntry>

    @Query("SELECT * FROM habit_entries WHERE habit_id = :habitId AND date = :date")
    fun getHabitEntriesForDate(habitId: Int, date: Long): List<HabitEntry>

    @Query("SELECT * FROM habit_entries WHERE habit_id IN (:habitIds) AND date >= :fromDate AND date <= :toDate")
    fun getHabitEntriesForDateRange(habitIds: IntArray, fromDate: Long, toDate: Long): List<HabitEntry>

    @Insert
    fun insertAll(vararg habitEntries: HabitEntry)

    @Update
    fun updateHabitEntry(habitEntry: HabitEntry)

    @Delete
    fun delete(habitEntry: HabitEntry)
}