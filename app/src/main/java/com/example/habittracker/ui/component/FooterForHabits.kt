package com.example.habittracker.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FooterForHabits() {
    var showAddHabitView by remember {
        mutableStateOf(false)
    }

    Card(
        shape = CardDefaults.elevatedShape,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.onPrimary, contentColor = MaterialTheme.colorScheme.onBackground),
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
                onClick = {
                          showAddHabitView = true
                },
                containerColor = MaterialTheme.colorScheme.tertiary
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
        if (showAddHabitView) {
            AddHabitSheet(onDismiss = {showAddHabitView = false})
        }
    }
}