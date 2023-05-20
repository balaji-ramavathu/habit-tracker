package com.example.habittracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.entity.Habit
import com.example.habittracker.data.entity.HabitReminderInfo
import com.example.habittracker.data.entity.HabitRepeatInfo
import com.example.habittracker.data.entity.HabitRepeatType
import com.example.habittracker.data.repository.HabitRepository
import com.example.habittracker.ui.model.AddOrUpdateHabitRequest
import com.example.habittracker.ui.model.HabitListItem
import com.example.habittracker.ui.utils.getDatesAndDaysForMonth
import com.example.habittracker.ui.utils.getMonthNameShort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

class MainViewModel(private val habitRepository: HabitRepository): ViewModel() {

    private val _habits = MutableLiveData<List<Habit>>()
    val habits: LiveData<List<Habit>>
        get() = _habits

    private val calendar = Calendar.getInstance()

    private val _currentMonth = MutableLiveData(calendar.get(Calendar.MONTH) + 1)
    val currentMonth: LiveData<Int>
        get() = _currentMonth

    private val _currentYear = MutableLiveData(calendar.get(Calendar.YEAR))
    val currentYear: LiveData<Int>
        get() = _currentYear

    private val _showAddOrUpdateAddHabitView = MutableLiveData(false)
    val showAddOrUpdateAddHabitView: LiveData<Boolean>
        get() = _showAddOrUpdateAddHabitView

    private val _addOrUpdateHabitData = MutableLiveData<AddOrUpdateHabitRequest>()
    val addOrUpdateHabitData: LiveData<AddOrUpdateHabitRequest>
        get() = _addOrUpdateHabitData

    private val _habitListItems = MutableLiveData<List<HabitListItem>>()
    val habitListItems: LiveData<List<HabitListItem>>
        get() = _habitListItems

    init {
        getHabits()
    }


    fun getHabits() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val habits = habitRepository.getHabits()
                _habits.postValue(habits)
                getHabitListItems(habits,
                    (_currentYear.value ?: calendar.get(Calendar.YEAR)),
                    (_currentMonth.value ?: calendar.get(Calendar.MONTH)))
            }
        }
    }

    fun getHabitListItems(habits: List<Habit>, year: Int, month: Int) {
        val itemList = mutableListOf<HabitListItem>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getDatesAndDaysForMonth(year, month).forEach { dateDay ->
                    val habitEntries = mutableMapOf<Int, Boolean>()
                    habits.forEach {
                        //todo:: get it from habitEntries
                        habitEntries[it.id] = false
                    }

                    itemList.add(
                        HabitListItem(
                            date = dateDay.first,
                            day = dateDay.second,
                            month = getMonthNameShort(month),
                            habitEntries = habitEntries
                        )
                    )
                }
            }
        }
        _habitListItems.postValue(itemList)
    }

    fun addHabit(addOrUpdateHabitRequest: AddOrUpdateHabitRequest) {

        validateRequest(addOrUpdateHabitRequest)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                habitRepository.addHabit(
                    Habit(
                        name = addOrUpdateHabitRequest.name,
                        indicator = addOrUpdateHabitRequest.name
                            .substring(0, 1)
                            .uppercase(Locale.ROOT),
                        color = addOrUpdateHabitRequest.color ?: "Green",       // TODO:: fix this
                        repeatInfo = HabitRepeatInfo(
                            repeatType = addOrUpdateHabitRequest.repeatType?.name?.let {
                                HabitRepeatType
                                    .valueOf(it)
                            } ?: HabitRepeatType.NONE
                        ),
                        reminderInfo = addOrUpdateHabitRequest.reminderTime?.let {
                            HabitReminderInfo(
                                it
                            )
                        },
                        addedAt = System.currentTimeMillis()
                    )
                )
                getHabits()
            }
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                habitRepository.updateHabit(habit)
            }
        }
    }

    fun archiveHabit(habit: Habit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                habitRepository.archiveHabit(habit)
            }
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                habitRepository.deleteHabit(habit)
            }
        }
    }

    fun showOrHideAddOrUpdateHabitView(show: Boolean) {
        _showAddOrUpdateAddHabitView.value = show
    }

    fun recordAddOrUpdateHabitRequestInfo(request: AddOrUpdateHabitRequest) {
        _addOrUpdateHabitData.value = request
    }

    private fun validateRequest(addOrUpdateHabitRequest: AddOrUpdateHabitRequest) {
        require(addOrUpdateHabitRequest.name.isNotBlank()) { "Habit name shouldn't be empty :: $addOrUpdateHabitRequest" }
    }
}