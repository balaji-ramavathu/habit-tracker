package com.habittracker.haby.di

import android.content.Context
import com.habittracker.haby.data.Database
import com.habittracker.haby.data.repository.HabitEntryRepository
import com.habittracker.haby.data.repository.HabitRepository

object Injection {
    fun provideHabitRepository(context: Context): HabitRepository {
        val database = Database.getInstance(context)
        val dao = database.habitDao()
        return HabitRepository.getInstance(dao)
    }

    fun provideHabitEntryRepository(context: Context): HabitEntryRepository {
        val database = Database.getInstance(context)
        val dao = database.habitEntryDao()
        return HabitEntryRepository.getInstance(dao)
    }
}