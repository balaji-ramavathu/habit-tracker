package com.example.habittracker.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.habittracker.ui.model.HabitEntryState
import com.example.habittracker.ui.model.HabitEntryState.APPLICABLE_AND_INCOMPLETE
import com.example.habittracker.ui.model.HabitEntryState.COMPLETED
import com.example.habittracker.ui.model.HabitEntryState.NOT_APPLICABLE
import com.example.habittracker.ui.utils.getDateMillis
import com.example.habittracker.ui.viewmodel.MainViewModel
import java.util.Calendar

@Composable
fun HabitEntryCheckbox(
    habitEntryState: HabitEntryState,
    mainViewModel: MainViewModel,
    habitId: Int,
    date: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val boxSize = 40.dp
    val tickSize = 20.dp
    val calendar = Calendar.getInstance()
    val currentYear = mainViewModel.currentYear.observeAsState(calendar.get(Calendar.YEAR))
    val currentMonth = mainViewModel.currentMonth.observeAsState(calendar.get(Calendar.MONTH) + 1)
    val updatedChecked = rememberUpdatedState(habitEntryState)

    Box(
        modifier = modifier
            .size(boxSize)
            .clip(CircleShape)
            .background(
                if (enabled) {
                    when(updatedChecked.value) {
                        COMPLETED -> {
                            MaterialTheme.colorScheme.secondary
                        }
                        APPLICABLE_AND_INCOMPLETE -> {
                            Color.LightGray
                        }
                        NOT_APPLICABLE -> {
                            MaterialTheme.colorScheme.secondary
                        }
                    }
                } else {
                    Color.Gray
                }
            )
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    mainViewModel.addOrUpdateHabitEntry(
                        habitId,
                        getDateMillis(currentYear.value, currentMonth.value, date),
                        updatedChecked.value != COMPLETED
                    )

                })
            },
        contentAlignment = Alignment.Center
    ) {
        when (updatedChecked.value) {
            COMPLETED -> {
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
                            .alpha(1f)
                            .animateContentSize()
                    )
                }
            }
            NOT_APPLICABLE -> {
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
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .size(tickSize)
                            .padding(2.dp)
                            .alpha(1f)
                            .animateContentSize()
                    )
                }
            }
            else -> {
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
                            .alpha(1f)
                            .animateContentSize()
                    )
                }
            }
        }
    }
}