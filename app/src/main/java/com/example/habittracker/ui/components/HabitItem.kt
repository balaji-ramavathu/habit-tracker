package com.example.habittracker.ui.components

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
import com.example.habittracker.models.HabitData
import java.util.Calendar

@Composable
fun HabitItem(habit: HabitData, highlightDivision: Boolean) {
    val calendar = Calendar.getInstance().apply {
        time = habit.date
    }
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = dayOfMonth.toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.width(30.dp)
        )
        Text(
            text = habit.day.toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.width(30.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(2f)
        ) {
            habit.habits.forEach { habitName ->
                CustomCheckbox(
                    checked = habitName.done,
                    onCheckedChange = { checked ->
                        habitName.done = checked
                    },
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
    Divider(color = if (highlightDivision) MaterialTheme.colorScheme.secondary else Color.LightGray)
}