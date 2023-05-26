package com.example.habittracker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.habittracker.data.entity.Habit
import com.example.habittracker.data.entity.HabitEntry
import com.example.habittracker.data.entity.HabitReminderInfo
import com.example.habittracker.data.entity.HabitRepeatInfo
import com.example.habittracker.data.entity.HabitRepeatType
import com.example.habittracker.data.repository.HabitEntryRepository
import com.example.habittracker.data.repository.HabitRepository
import com.example.habittracker.data.worker.HabitNotificationWorker
import com.example.habittracker.ui.model.AddOrUpdateHabitRequest
import com.example.habittracker.ui.model.HabitListItem
import com.example.habittracker.ui.utils.getDateMillis
import com.example.habittracker.ui.utils.getDatesAndDaysForMonth
import com.example.habittracker.ui.utils.getDurationLeftFromNow
import com.example.habittracker.ui.utils.getFirstAndLastDayOfMonth
import com.example.habittracker.ui.utils.getMonthNameShort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainViewModel(
    private val habitRepository: HabitRepository,
    private val habitEntryRepository: HabitEntryRepository,
    private val application: Application
): AndroidViewModel(application) {

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

    private val workManager = WorkManager.getInstance(application.applicationContext)

    init {
        getHabits()
    }


    private fun getHabits() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val habits = habitRepository.getHabits()
                _habits.postValue(habits)
                getHabitListItems(habits,
                    (_currentYear.value ?: calendar.get(Calendar.YEAR)),
                    ((_currentMonth.value ?: (calendar.get(Calendar.MONTH) + 1))))
            }
        }
    }

    fun getHabitListItems(habits: List<Habit>, year: Int, month: Int) {
        viewModelScope.launch {
            val itemList = withContext(Dispatchers.IO) {
                val firstAndLastDates = getFirstAndLastDayOfMonth(year, month)
                val habitEntriesMap = getHabitEntriesForDateRange(
                    habits.map { it.id },
                    firstAndLastDates.first,
                    firstAndLastDates.second
                ).groupBy { it.habitId }

                getDatesAndDaysForMonth(year, month).map { dateDay ->
                    val habitEntries = habits.associate { habit ->
                        habit.id to (habitEntriesMap[habit.id]?.firstOrNull { entry ->
                            entry.date == getDateMillis(year, month, dateDay.first)
                        }?.completed ?: false)
                    }

                    HabitListItem(
                        date = dateDay.first,
                        day = dateDay.second,
                        monthName = getMonthNameShort(month),
                        month = month,
                        year = year,
                        habitEntries = habitEntries
                    )
                }
            }
            _habitListItems.value = itemList
        }
    }


    fun addOrUpdateHabit(addOrUpdateHabitRequest: AddOrUpdateHabitRequest) {

        validateRequest(addOrUpdateHabitRequest)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (addOrUpdateHabitRequest.id != null) {
                    val habit = habitRepository.getHabit(addOrUpdateHabitRequest.id)

                    if (habit != null) {
                        val updatedHabit = habit.copy(
                            name = addOrUpdateHabitRequest.name,
                            indicator = addOrUpdateHabitRequest.name
                                .substring(0, 1)
                                .uppercase(Locale.ROOT),
                            color = addOrUpdateHabitRequest.color ?: "Green",       // TODO:: fix this
                            repeatInfo = HabitRepeatInfo(
                                repeatType = addOrUpdateHabitRequest.repeatType?.name?.let {
                                    HabitRepeatType
                                        .valueOf(it)
                                } ?: HabitRepeatType.DAILY
                            ),
                            reminderInfo = addOrUpdateHabitRequest.reminderTime?.let {
                                HabitReminderInfo(
                                    it
                                )
                            },
                            addedAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        habitRepository.updateHabit(updatedHabit).also {
                            scheduleReminder(updatedHabit)
                            if (_habits.value != null) {
                                val newList = _habits.value!!.toMutableList()
                                val index = _habits.value!!.indexOf(habit)
                                newList[index] = updatedHabit
                                _habits.postValue(newList)
                            }

                        }
                    }
                } else {
                    val newHabit = Habit(
                        name = addOrUpdateHabitRequest.name,
                        indicator = addOrUpdateHabitRequest.name
                            .substring(0, 1)
                            .uppercase(Locale.ROOT),
                        color = addOrUpdateHabitRequest.color ?: "Green",       // TODO:: fix this
                        repeatInfo = HabitRepeatInfo(
                            repeatType = addOrUpdateHabitRequest.repeatType?.name?.let {
                                HabitRepeatType
                                    .valueOf(it)
                            } ?: HabitRepeatType.DAILY
                        ),
                        reminderInfo = addOrUpdateHabitRequest.reminderTime?.let {
                            HabitReminderInfo(
                                it
                            )
                        },
                        addedAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    habitRepository.addHabit(newHabit).also {
                        scheduleReminder(newHabit)
                        if (_habits.value != null) {
                            _habits.postValue(_habits.value!!.plus(newHabit))
                        }
                    }
                }
            }
        }
    }

    private fun scheduleReminder(habit: Habit) {
        if (habit.reminderInfo == null) {
            workManager.cancelAllWorkByTag(habit.id.toString())
        } else {
            val scheduled = workManager.getWorkInfosByTag(habit.id.toString()).get()
            if (scheduled.isEmpty() || scheduled.all { it.state.isFinished }) {
                scheduleOrRescheduleNotificationForHabit(habit)
            }
            else {
                val unfinished = scheduled.filter { !it.state.isFinished }
                if (unfinished.isNotEmpty()) {
                    cancelAndRescheduleNotification(habit)
                }
            }
        }
    }

    private fun scheduleOrRescheduleNotificationForHabit(habit: Habit) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val inputData = Data.Builder()
            .putInt("habitId", habit.id)
            .putString("reminderTime", habit.reminderInfo?.reminderTime)
            .build()

        val desiredTimeObj = LocalTime.parse(habit.reminderInfo?.reminderTime)
        val initialDelay = getDurationLeftFromNow(desiredTimeObj)

        val request = PeriodicWorkRequestBuilder<HabitNotificationWorker>(
            getDaysForRepeatType(habit.repeatInfo.repeatType).toLong(), TimeUnit.DAYS)
            .setConstraints(constraints)
            .setInputData(inputData)
            .addTag(habit.id.toString())
            .setInitialDelay(initialDelay.toMillis(), TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueue(request)
    }



    private fun cancelAndRescheduleNotification(habit: Habit) {
        workManager.cancelAllWorkByTag(habit.id.toString())

        scheduleOrRescheduleNotificationForHabit(habit)
    }

    private fun getDaysForRepeatType(repeatType: HabitRepeatType): Int {
        return when (repeatType) {
            HabitRepeatType.DAILY -> 1
            HabitRepeatType.WEEKLY -> 7
            HabitRepeatType.MONTHLY -> 30
            HabitRepeatType.YEARLY -> 365
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

    fun deleteHabit(habitId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                habitRepository.getHabit(habitId)?.let { habitRepository.deleteHabit(it) }
                getHabits()
            }
        }
    }

    fun showOrHideAddOrUpdateHabitView(show: Boolean) {
        _showAddOrUpdateAddHabitView.value = show
    }

    fun recordAddOrUpdateHabitRequestInfo(request: AddOrUpdateHabitRequest?) {
        _addOrUpdateHabitData.value = request
    }

    fun addOrUpdateHabitEntry(habitId: Int, date: Long, completed: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val entry = habitEntryRepository.getHabitEntryForDate(habitId, date)
                if (entry == null) {
                    habitEntryRepository.insertHabitEntry(
                        HabitEntry(
                            habitId = habitId,
                            completed = completed,
                            date = date,
                            addedAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                } else {
                    habitEntryRepository.insertHabitEntry(
                        entry.copy(
                            completed = completed,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
                updateHabitListItem(habitId, date, completed)
            }
        }
    }

    private fun updateHabitListItem(habitId: Int, date: Long, completed: Boolean) {
        _habitListItems.postValue(
            _habitListItems.value?.let { habitList ->
                val updatedList = habitList.toMutableList()
                val habitListItemIndex = updatedList.indexOfFirst { getDateMillis(it.year, it.month, it.date) == date }

                if (habitListItemIndex != -1) {
                    val habitListItem = updatedList[habitListItemIndex]
                    val updatedHabitEntries = habitListItem.habitEntries.toMutableMap()

                    updatedHabitEntries[habitId] = completed
                    val updatedHabitListItem = habitListItem.copy(habitEntries = updatedHabitEntries)

                    updatedList[habitListItemIndex] = updatedHabitListItem
                }
                updatedList
            }
        )
    }

    private fun getHabitEntriesForDateRange(
        habitIds: List<Int>,
        fromDate: Long,
        toDate: Long
    ): List<HabitEntry> {
        return habitEntryRepository.getHabitEntriesForDateRange(habitIds, fromDate, toDate)
    }

    private fun validateRequest(addOrUpdateHabitRequest: AddOrUpdateHabitRequest) {
        require(addOrUpdateHabitRequest.name.isNotBlank()) { "Habit name shouldn't be empty :: $addOrUpdateHabitRequest" }
    }
}