package com.example.habittracker.data.repository

import com.example.habittracker.data.dao.HabitEntryDao
import com.example.habittracker.data.entity.HabitEntry

class HabitEntryRepository(private val habitEntryDao: HabitEntryDao) {

    fun getHabitEntries(habitId: Int): List<HabitEntry> {
        return habitEntryDao.loadAllByHabitIds(intArrayOf(habitId))
    }

    fun getHabitEntryForDate(habitId: Int, date: Long) {

    }

}