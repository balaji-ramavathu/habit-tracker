package com.habittracker.haby.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habittracker.haby.ui.model.HabitListItem
import com.habittracker.haby.ui.viewmodel.MainViewModel


@Composable
fun HabitItem(habit: HabitListItem,
              highlightDivision: Boolean,
              mainViewModel: MainViewModel) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = habit.date.toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.width(30.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = habit.day,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.width(30.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(2f)
        ) {
            items(habit.habitEntries.toList(),
                key = {habitEntry -> "${habitEntry.first}::${habit.date}"}) {habitEntry ->
                val (habitId, habitEntryState) = habitEntry
                HabitEntryCheckbox(
                    habitId = habitId,
                    date = habit.date,
                    habitEntryState = habitEntryState,
                    mainViewModel = mainViewModel,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
    Divider(color = if (highlightDivision) MaterialTheme.colorScheme.secondary else Color.LightGray)
}