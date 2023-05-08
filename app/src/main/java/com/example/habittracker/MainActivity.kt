package com.example.habittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.habittracker.model.HabitData
import com.example.habittracker.model.HabitName
import com.example.habittracker.ui.component.FooterForHabits
import com.example.habittracker.ui.component.HabitList
import com.example.habittracker.ui.component.TopBar
import com.example.habittracker.ui.theme.HabitTrackerTheme
import java.util.Calendar
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val habitData = mutableListOf<HabitData>()
        getDaysOfMonth().forEach {
            habitData.add(HabitData(it, 'S', listOf(
                HabitName("Exercise", "E", true),
                HabitName("Meditation", "M", false),
                HabitName("Exercise", "F", false),
                HabitName("Exercise", "S", true),
                HabitName("Exercise", "B", true)
            )))
        }
        setContent {
            HabitTrackerTheme {
                MainLayout(habitData = habitData)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(habitData: List<HabitData>) {
    Scaffold(topBar = {
        TopBar()
    }, floatingActionButton = {
        FooterForHabits()
    }, floatingActionButtonPosition = FabPosition.Center) { padding ->
        Box(modifier = Modifier
            .padding(paddingValues = padding)
            .background(MaterialTheme.colorScheme.secondary)) {
            HabitList(habitData = habitData)
        }
    }
}


private fun getDaysOfMonth(): List<Date> {
    val calendar = Calendar.getInstance()
    val currentDate = Date()
    calendar.time = currentDate
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val daysList = mutableListOf<Date>()

    for (day in 1..daysInMonth) {
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val date = calendar.time
        daysList.add(date)
    }
    return daysList
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val habitData = mutableListOf<HabitData>()
    getDaysOfMonth().forEach {
        habitData.add(HabitData(it, 'S', listOf(
            HabitName("Exercise", "E", true),
            HabitName("Meditation", "M", false),
            HabitName("Exercise", "F", false),
            HabitName("Exercise", "S", true),
            HabitName("Exercise", "B", true)
        )))
    }

    HabitTrackerTheme {
        MainLayout(habitData = habitData)
    }
}