package com.example.habittracker.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.ui.model.AddOrUpdateHabitRequest
import com.example.habittracker.ui.model.UiRepeatType
import com.example.habittracker.ui.utils.getMonthNameShort
import com.example.habittracker.ui.viewmodel.MainViewModel
import java.util.Calendar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitList(mainViewModel: MainViewModel, isFabVisible: MutableState<Boolean>) {

    val habits = mainViewModel.habits.observeAsState(listOf())
    val habitList = mainViewModel.habitListItems.observeAsState(listOf())
    val habitIndicatorsListLazyState = rememberLazyListState()
    val habitItemsListLazyState = rememberLazyListState()
    val calendar = Calendar.getInstance()
    val currentYear = mainViewModel.currentYear.observeAsState(calendar.get(Calendar.YEAR))
    val currentMonth = mainViewModel.currentMonth.observeAsState(calendar.get(Calendar.MONTH) + 1)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.primary
            )
    ) {
        LazyColumn(state = habitItemsListLazyState) {
            stickyHeader {
                Column(
                    Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary
                        )) {
                    Row (horizontalArrangement = Arrangement.End, modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = getMonthNameShort(mainViewModel.currentMonth.value).uppercase(),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        )
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowLeft,
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = null
                            )
                        }
                    }
                    Row (modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)) {
                        Spacer(modifier = Modifier.width(96.dp))
                        Box(Modifier.width(250.dp)) {
                            LazyRow(state = habitIndicatorsListLazyState) {
                                items(habits.value) {habit ->
                                    Box(
                                        Modifier.padding(horizontal = 4.dp)
                                        .pointerInput(Unit) {
                                        detectTapGestures(onLongPress = {
                                            mainViewModel.recordAddOrUpdateHabitRequestInfo(
                                                AddOrUpdateHabitRequest(
                                                    id = habit.id,
                                                    name = habit.name,
                                                    color = habit.color,
                                                    repeatType = habit.repeatInfo.repeatType.let {
                                                        UiRepeatType.valueOf(it.name) },
                                                    repeatValue = habit.repeatInfo.repeatValue,
                                                    reminderTime = habit.reminderInfo?.reminderTime
                                                )
                                            ).also {
                                                mainViewModel.showOrHideAddOrUpdateHabitView(true)
                                            }
                                        })
                                    }
                                    ) {
                                        Text(
                                            text = habit.indicator,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            modifier = Modifier.width(24.dp),
                                            color = MaterialTheme.colorScheme.onBackground)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            items(habitList.value) { habit ->
                HabitItem(
                    mainViewModel = mainViewModel,
                    habit = habit,
                    highlightDivision = ((habitList.value.indexOf(habit) + 1) % 6 == 0)
                )
            }
        }
    }
    
    LaunchedEffect(habitIndicatorsListLazyState, habits.value, currentMonth.value, currentYear.value, habitList.value) {
        val firstVisibleItem = habitIndicatorsListLazyState.firstVisibleItemIndex
        val lastVisibleItem = habitIndicatorsListLazyState.firstVisibleItemIndex + habitIndicatorsListLazyState.layoutInfo.visibleItemsInfo.size - 1
        val visibleItems = habits.value.subList(firstVisibleItem, lastVisibleItem + 1)
        mainViewModel.getHabitListItems(visibleItems, currentYear.value, currentMonth.value)
    }

    LaunchedEffect(habitItemsListLazyState) {
        snapshotFlow { habitItemsListLazyState.firstVisibleItemIndex }.collect { index ->
                isFabVisible.value = index == 0
        }
    }
}