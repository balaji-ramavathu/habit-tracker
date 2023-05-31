package com.habittracker.haby.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.habittracker.haby.ui.theme.HabitTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {

    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.secondary),
        title = { Text(
            "Habits",
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Left,
            modifier = Modifier.fillMaxWidth()
        )}
    )
}

@Preview
@Composable
fun TopBarPreview() {
    HabitTrackerTheme {
        TopBar()
    }
}
