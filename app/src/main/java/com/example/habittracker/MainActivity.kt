package com.example.habittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.habittracker.ui.component.FloatingActionView
import com.example.habittracker.ui.component.HabitList
import com.example.habittracker.ui.component.TopBar
import com.example.habittracker.ui.theme.HabitTrackerTheme
import com.example.habittracker.ui.viewmodel.MainViewModel
import com.example.habittracker.ui.viewmodel.ViewModelFactory


class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HabitTrackerTheme {
                MainLayout(viewModel)
            }
        }
    }
}

@Composable
fun MainLayout(viewModel: MainViewModel) {
    val isFabVisible = remember { mutableStateOf(true) }

    Scaffold(topBar = {
        TopBar()
    }, floatingActionButton = {
        AnimatedVisibility(visible = isFabVisible.value, enter = fadeIn(), exit = fadeOut()) {
            FloatingActionView(viewModel)
        }
    }, floatingActionButtonPosition = FabPosition.Center) { padding ->
        Box(modifier = Modifier
            .padding(paddingValues = padding)
            .background(MaterialTheme.colorScheme.secondary)) {
            HabitList(viewModel, isFabVisible)
        }
    }
}