package com.habittracker.haby.data.repository

import com.habittracker.haby.data.dao.HabitDao
import com.habittracker.haby.data.entity.Habit

class HabitRepository (private val habitDao: HabitDao) {

    fun getHabits(includeArchived: Boolean = false): List<Habit> {
       return if (includeArchived) habitDao.getAll() else habitDao.getAllActive()
    }

    fun getHabit(id: Int): Habit? {
        return habitDao.byId(id)
    }

    fun addHabit(habit: Habit): Long {
        require(validate(habit)) { "Invalid Habit input request" }

        return habitDao.insertHabit(habit)
    }

    fun updateHabit(habit: Habit) {
        require(validate(habit)) { "Invalid Habit input request" }

        habitDao.updateHabit(habit)
    }
    fun archiveHabit(habit: Habit) {
        habitDao.updateHabit(habit.copy(archived = true))
    }

    fun deleteHabit(habit: Habit) {
        habitDao.delete(habit)
    }

    private fun validate(habit: Habit): Boolean {
        require(habit.name.isNotBlank()) { "Habit name shouldn't be empty" }
        require(habit.indicator.isNotBlank()) { "Habit indicator shouldn't be empty" }
        require(habit.color.isNotBlank()) { "Habit color shouldn't be empty" }

        return true
    }

    companion object {
        @Volatile
        private var instance: HabitRepository? = null
        fun getInstance(dao: HabitDao): HabitRepository =
            instance ?: synchronized(this) {
                instance ?: HabitRepository(dao)
            }.also { instance = it }
    }
}