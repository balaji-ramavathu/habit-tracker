package com.example.habittracker.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.example.habittracker.ui.model.HabitListItem

@Composable
fun HabitItem(habit: HabitListItem, highlightDivision: Boolean) {

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
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(2f)
        ) {
            habit.habitEntries.forEach { habitEntry ->
                CustomCheckbox(
                    checked = habitEntry.value,
                    onCheckedChange = { checked ->
                        // TODO
                    },
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
    Divider(color = if (highlightDivision) MaterialTheme.colorScheme.secondary else Color.LightGray)
}