package com.example.habittracker.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.models.HabitData
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitList(habitData: List<HabitData>) {
    val habitIndicators = if (habitData.isEmpty()) emptyList() else {
        habitData[0].habits.map { it.indicator }
    }

    val currentMonth = remember {
        getCurrentMonth()
    }
    val currentMonthHabits = remember {
        groupHabitsByMonth(habitData)[currentMonth] ?: emptyList()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.primary
            )
    ) {
        LazyColumn {
            stickyHeader {
                Column(
                    Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary
                        )) {
                    Row (horizontalArrangement = Arrangement.End, modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = currentMonth,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .weight(1f).padding(8.dp)
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
                    Row (horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)) {
                        Spacer(modifier = Modifier.width(96.dp))
                        habitIndicators.forEach {
                            Text(
                                text = it,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.width(24.dp),
                                color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }
            }
            items(currentMonthHabits) { habit ->
                HabitItem(habit = habit, (currentMonthHabits.indexOf(habit) % 7 == 0))
            }
        }
    }
}

private fun groupHabitsByMonth(
    habits: List<HabitData>
): Map<String, List<HabitData>> {
    return habits.groupBy { habit ->
        val calendar = Calendar.getInstance()
        calendar.time = habit.date
        val month = calendar.getDisplayName(
            Calendar.MONTH,
            Calendar.LONG,
            Locale.getDefault()
        )?.uppercase(Locale.ROOT)
        "$month"
    }
}

private fun getCurrentMonth(): String {
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    return calendar.getDisplayName(
        Calendar.MONTH,
        Calendar.LONG,
        Locale.getDefault())?.uppercase(Locale.ROOT).toString()
}