package com.habittracker.haby.data.repository

import com.habittracker.haby.data.dao.HabitEntryDao
import com.habittracker.haby.data.entity.HabitEntry

class HabitEntryRepository(private val habitEntryDao: HabitEntryDao) {

    fun getHabitEntries(habitId: Int): List<HabitEntry> {
        return habitEntryDao.loadAllByHabitIds(intArrayOf(habitId))
    }
    fun getHabitEntryForDate(habitId: Int, date: Long): HabitEntry? {
        return habitEntryDao.getHabitEntriesForDate(habitId, date).lastOrNull()
    }

    fun getHabitEntriesForDateRange(habitIds: List<Int>,
                                    fromDate: Long, toDate: Long): List<HabitEntry> {
        return habitEntryDao.getHabitEntriesForDateRange(habitIds.toIntArray(), fromDate, toDate)
    }

    fun insertHabitEntry(habitEntry: HabitEntry) {
        habitEntryDao.insertHabitEntry(habitEntry)
    }

    fun updateHabitEntry(habitEntry: HabitEntry) {
        habitEntryDao.updateHabitEntry(habitEntry)
    }

    fun deleteHabitEntry(habitEntry: HabitEntry) {
        habitEntryDao.delete(habitEntry)
    }

    companion object {
        @Volatile
        private var instance: HabitEntryRepository? = null
        fun getInstance(dao: HabitEntryDao): HabitEntryRepository =
            instance ?: synchronized(this) {
                instance ?: HabitEntryRepository(dao)
            }.also { instance = it }
    }

}