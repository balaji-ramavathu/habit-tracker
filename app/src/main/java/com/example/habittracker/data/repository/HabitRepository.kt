package com.example.habittracker.data.repository

import com.example.habittracker.data.dao.HabitDao
import com.example.habittracker.data.entity.Habit

class HabitRepository (private val habitDao: HabitDao) {

    fun getHabits(includeArchived: Boolean = false): List<Habit> {
       return if (includeArchived) habitDao.getAll() else habitDao.getAllActive()
    }

    fun getHabit(id: Int): Habit? {
        return habitDao.byId(id)
    }

    suspend fun addOrUpdateNewHabit(habit: Habit): Habit {
        require(validate(habit)) { "Invalid Habit input request" }

        return habitDao.insertHabit(habit)
    }

    private fun validate(habit: Habit): Boolean {
        require(habit.name.isBlank()) { "Habit name shouldn't be empty" }
        require(habit.indicator.isBlank()) { "Habit indicator shouldn't be empty" }
        require(habit.color.isBlank()) { "Habit color shouldn't be empty" }

        return true
    }
}