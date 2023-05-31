package com.habittracker.haby.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.habittracker.haby.data.repository.HabitEntryRepository
import com.habittracker.haby.data.repository.HabitRepository
import com.habittracker.haby.di.Injection

class ViewModelFactory(
    private val habitRepository: HabitRepository,
    private val habitEntryRepository: HabitEntryRepository,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(
                habitRepository, habitEntryRepository, application
            ) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context, application: Application): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideHabitRepository(context),
                    Injection.provideHabitEntryRepository(context),
                    application
                )
            }.also { instance = it }
    }
}