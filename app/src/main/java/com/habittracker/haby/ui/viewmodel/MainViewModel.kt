package com.habittracker.haby.ui.viewmodel

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
import com.habittracker.haby.data.entity.Habit
import com.habittracker.haby.data.entity.HabitEntry
import com.habittracker.haby.data.entity.HabitReminderInfo
import com.habittracker.haby.data.entity.HabitRepeatInfo
import com.habittracker.haby.data.entity.HabitRepeatType
import com.habittracker.haby.data.repository.HabitEntryRepository
import com.habittracker.haby.data.repository.HabitRepository
import com.habittracker.haby.data.worker.HabitNotificationWorker
import com.habittracker.haby.ui.model.AddOrUpdateHabitRequest
import com.habittracker.haby.ui.model.HabitEntryState
import com.habittracker.haby.ui.model.HabitListItem
import com.habittracker.haby.ui.utils.getDateMillis
import com.habittracker.haby.ui.utils.getDatesAndDaysForMonth
import com.habittracker.haby.ui.utils.getDaysBetweenTwoDates
import com.habittracker.haby.ui.utils.getDurationLeftFromNow
import com.habittracker.haby.ui.utils.getFirstAndLastDayOfMonth
import com.habittracker.haby.ui.utils.getNextMonthAndYear
import com.habittracker.haby.ui.utils.getPreviousMonthAndYear
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
                        val dateMillis = getDateMillis(year, month, dateDay.first)
                        val completed = (habitEntriesMap[habit.id]?.firstOrNull { entry ->
                            entry.date == dateMillis
                        }?.completed ?: false)
                        val state = if (completed) {
                            HabitEntryState.COMPLETED
                        } else {
                            val previousLatestEntry = habitEntriesMap[habit.id]
                                ?.filter { it.date < dateMillis && it.completed }
                                ?.maxByOrNull { it.date }
                            if (previousLatestEntry == null) {
                                HabitEntryState.APPLICABLE_AND_INCOMPLETE
                            }
                            else if (!habitApplicable(previousLatestEntry, dateMillis, habit.repeatInfo)) {
                                HabitEntryState.NOT_APPLICABLE
                            } else {
                                HabitEntryState.APPLICABLE_AND_INCOMPLETE
                            }
                        }
                        habit.id to state
                    }

                    HabitListItem(
                        date = dateDay.first,
                        day = dateDay.second,
                        month = month,
                        year = year,
                        habitEntries = habitEntries
                    )
                }
            }
            _habitListItems.value = itemList
        }
    }

    private fun habitApplicable(
        previousLatestEntry: HabitEntry,
        currentHabitEntryDate: Long,
        repeatInfo: HabitRepeatInfo
    ): Boolean {
        val applicableLimit = getDaysForRepeatType(repeatInfo.repeatType)
        return getDaysBetweenTwoDates(previousLatestEntry.date, currentHabitEntryDate) >= applicableLimit
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
                                    HabitRepeatType.valueOf(it)
                                } ?: HabitRepeatType.DAILY
                            ),
                            reminderInfo = addOrUpdateHabitRequest.reminderTime?.let {
                                HabitReminderInfo(
                                    it
                                )
                            },
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
                    val generatedId = habitRepository.addHabit(newHabit)
                    val updatedHabit = newHabit.copy(id = generatedId.toInt())
                    scheduleReminder(updatedHabit)
                    if (_habits.value != null) {
                        _habits.postValue(_habits.value!!.plus(updatedHabit))
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
            .putString("habitName", habit.name)
            .putLong("habitDate", getDateMillis(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DATE)
            ))
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
                habitEntryRepository.getHabitEntries(habitId).forEach {
                    habitEntryRepository.deleteHabitEntry(it)
                }
                workManager.cancelAllWorkByTag(habitId.toString())
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

    fun updateCurrentMonthAndYear(directionIsNext: Boolean) {
        val updated = if (directionIsNext) {
            getNextMonthAndYear(
                currentYear = (_currentYear.value ?: calendar.get(Calendar.YEAR)),
                currentMonth = ((_currentMonth.value ?: (calendar.get(Calendar.MONTH) + 1))))
        } else {
            getPreviousMonthAndYear(
                currentYear = (_currentYear.value ?: calendar.get(Calendar.YEAR)),
                currentMonth = ((_currentMonth.value ?: (calendar.get(Calendar.MONTH) + 1))))
        }

        _currentMonth.postValue(updated.first)
        _currentYear.postValue(updated.second)
    }

    private fun updateHabitListItem(habitId: Int, date: Long, completed: Boolean) {
        _habitListItems.postValue(
            _habitListItems.value?.let { habitList ->
                val updatedList = habitList.toMutableList()
                val habitListItemIndex = updatedList.indexOfFirst { getDateMillis(it.year, it.month, it.date) == date }

                if (habitListItemIndex != -1) {
                    val habitListItem = updatedList[habitListItemIndex]
                    val updatedHabitEntries = habitListItem.habitEntries.toMutableMap()
                    val prevEntry = updatedHabitEntries[habitId]

                    updatedHabitEntries[habitId] = if (completed) {
                        HabitEntryState.COMPLETED
                    } else {
                        if (prevEntry == HabitEntryState.NOT_APPLICABLE) {
                            HabitEntryState.NOT_APPLICABLE
                        } else {
                            HabitEntryState.APPLICABLE_AND_INCOMPLETE
                        }
                    }
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