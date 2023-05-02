package com.example.habittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.ui.theme.HabitTrackerTheme
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
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
        }
    }
}

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
@Composable
fun CustomCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val boxSize = 40.dp
    val tickSize = 20.dp

    Box(
        modifier = modifier
            .size(boxSize)
            .clip(CircleShape)
            .background(
                if (enabled) {
                    if (checked) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        Color.LightGray
                    }
                } else {
                    Color.Gray
                }
            )
            .clickable(enabled = enabled) { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Box(
                modifier = Modifier
                    .size(tickSize)
                    .background(
                        MaterialTheme.colorScheme.secondary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .size(tickSize)
                        .padding(2.dp)
                        .alpha(if (checked) 1f else 0f)
                        .animateContentSize()
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(tickSize)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(tickSize * 0.6f)
                        .alpha(if (checked) 0f else 1f)
                        .animateContentSize()
                )
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

@Composable
fun FooterForHabits() {
    Card(
        shape = CardDefaults.elevatedShape,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.onPrimary, contentColor = Color.Black),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.height(60.dp)) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(text = "B - Books",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp)
                    Text(text = "E - Exercise",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp)
                    Text(text = "M - Meditation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp)
                    Text(text = "G - Engg",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp)
                    Text(text = "E - Exercise",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp)
                    Text(text = "M - Meditation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp)
                }
            }
            FloatingActionButton(
                onClick = { /* do something */ },
                containerColor = MaterialTheme.colorScheme.tertiary
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
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
data class HabitData(
    val date: Date,
    val day: Char,
    val habits: List<HabitName>
)

data class HabitName(
    val name: String,
    val indicator: String,
    var done: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {

    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.secondary),
        title = { Text(
            "Habits",
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )},
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
}