package com.example.habittracker.di

import android.content.Context
import com.example.habittracker.data.Database
import com.example.habittracker.data.repository.HabitEntryRepository
import com.example.habittracker.data.repository.HabitRepository

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