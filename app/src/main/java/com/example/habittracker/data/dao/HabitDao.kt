package com.example.habittracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.habittracker.data.entity.Habit

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Habit

    @Query("SELECT * FROM habits")
    fun getAll(): List<Habit>

    @Query("SELECT * FROM habits where id = :id")
    fun byId(id: Int): Habit?

    @Query("SELECT * FROM habits where archived = 'false'")
    fun getAllActive(): List<Habit>

    @Query("SELECT * FROM habits WHERE id IN (:habitIds)")
    fun loadAllByIds(habitIds: IntArray): List<Habit>

    @Query("SELECT * FROM habits WHERE name LIKE :first LIMIT 1")
    fun findByName(first: String): Habit

    @Insert
    fun insertAll(vararg habits: List<Habit>)

    @Delete
    fun delete(habit: Habit)
}