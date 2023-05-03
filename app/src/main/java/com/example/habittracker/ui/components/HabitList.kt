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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.models.HabitData
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitList(habitData: List<HabitData>) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            MaterialTheme.colorScheme.primary,
            RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        )) {
        LazyColumn (Modifier.padding(8.dp)) {
            itemsByMonth(habitData) { month, habits ->
                stickyHeader {
                    Column {
                        Row (horizontalArrangement = Arrangement.End) {
                            Text(
                                text = month,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f).padding(8.dp)
                            )
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowLeft,
                                    tint = Color.Black,
                                    contentDescription = null
                                )
                            }
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    tint = Color.Black,
                                    contentDescription = null
                                )
                            }
                        }
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(2.dp)) {
                            Row (horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Spacer(modifier = Modifier.width(96.dp))
                                habits[0].habits.forEach {
                                    Text(text = it.indicator,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.width(24.dp))
                                }
                            }
                        }
                    }
                }
                items(habits) { habit ->
                    HabitItem(habit = habit, (habits.indexOf(habit) % 7 == 0))
                }
            }
        }
    }
}

private fun itemsByMonth(
    habits: List<HabitData>,
    content: (String, List<HabitData>) -> Unit
) {
    val groupedHabits = habits.groupBy { habit ->
        val calendar = Calendar.getInstance()
        calendar.time = habit.date
        val month = calendar.getDisplayName(
            Calendar.MONTH,
            Calendar.LONG,
            Locale.getDefault()
        )?.uppercase(Locale.ROOT)
        "$month"
    }
    groupedHabits.forEach { (month, habits) ->
        content(month, habits)
    }
}