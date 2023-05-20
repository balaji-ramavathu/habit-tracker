package com.example.habittracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.habittracker.data.entity.Habit

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHabit(habit: Habit)

    @Query("SELECT * FROM habits")
    fun getAll(): List<Habit>

    @Query("SELECT * FROM habits where id = :id")
    fun byId(id: Int): Habit?

    @Query("SELECT * FROM habits where archived = :includeArchived")
    fun getAllActive(includeArchived: Boolean = false): List<Habit>

    @Query("SELECT * FROM habits WHERE id IN (:habitIds)")
    fun loadAllByIds(habitIds: IntArray): List<Habit>

    @Query("SELECT * FROM habits WHERE name LIKE :first LIMIT 1")
    fun findByName(first: String): Habit

    @Update
    fun updateHabit(habit: Habit)
    @Insert
    fun insertAll(vararg habits: Habit)

    @Delete
    fun delete(habit: Habit)
}