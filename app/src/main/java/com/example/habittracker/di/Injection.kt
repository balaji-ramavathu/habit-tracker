package com.example.habittracker.di

import android.content.Context
import com.example.habittracker.data.Database
import com.example.habittracker.data.repository.HabitRepository

object Injection {
    fun provideRepository(context: Context): HabitRepository {
        val database = Database.getInstance(context)
        val dao = database.habitDao()
        return HabitRepository.getInstance(dao)
    }
}