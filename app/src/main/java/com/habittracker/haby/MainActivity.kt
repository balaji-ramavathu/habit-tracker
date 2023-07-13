package com.habittracker.haby

import android.Manifest
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.habittracker.haby.data.Constants.REMINDER_INTENT_DATA_HABIT_COMPLETED
import com.habittracker.haby.data.Constants.REMINDER_INTENT_DATA_HABIT_DATE
import com.habittracker.haby.data.Constants.REMINDER_INTENT_DATA_HABIT_ID
import com.habittracker.haby.data.Constants.REMINDER_INTENT_DATA_NOTIFICATION_ID
import com.habittracker.haby.data.Constants.REMINDER_NO_ACTION_INTENT
import com.habittracker.haby.data.Constants.REMINDER_YES_ACTION_INTENT
import com.habittracker.haby.ui.component.AddOrUpdateHabitSheet
import com.habittracker.haby.ui.component.EmptyHabitList
import com.habittracker.haby.ui.component.FloatingActionView
import com.habittracker.haby.ui.component.HabitList
import com.habittracker.haby.ui.component.SnackbarWithoutScaffold
import com.habittracker.haby.ui.component.TopBar
import com.habittracker.haby.ui.theme.HabitTrackerTheme
import com.habittracker.haby.ui.viewmodel.MainViewModel
import com.habittracker.haby.ui.viewmodel.ViewModelFactory


class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this, application)
    }
    private lateinit var actionReceiver: BroadcastReceiver
    private val notificationPermissionRequestCode = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HabitTrackerTheme {
                MainLayout(viewModel)
            }
        }
        if (!isNotificationPermissionGranted()) {
            requestNotificationPermission()
        }
        actionReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val habitCompleted = intent?.getBooleanExtra(
                    REMINDER_INTENT_DATA_HABIT_COMPLETED,
                    false) ?: false
                val habitDate = intent?.getLongExtra(
                    REMINDER_INTENT_DATA_HABIT_DATE,
                    0L) ?: 0L
                val habitId = intent?.getIntExtra(REMINDER_INTENT_DATA_HABIT_ID,
                    0) ?: 0
                val notificationId = intent?.getIntExtra(
                    REMINDER_INTENT_DATA_NOTIFICATION_ID,
                    0) ?: 0
                viewModel.addOrUpdateHabitEntry(habitId, habitDate, habitCompleted)
                dismissNotification(notificationId)
            }
        }

        val actionFilter = IntentFilter().apply {
            addAction(REMINDER_YES_ACTION_INTENT)
            addAction(REMINDER_NO_ACTION_INTENT)
        }

        // Register the BroadcastReceiver
        registerReceiver(actionReceiver, actionFilter)

    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the BroadcastReceiver
        unregisterReceiver(actionReceiver)
    }

    private fun dismissNotification(notificationId: Int) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    private fun isNotificationPermissionGranted(): Boolean {
        val notificationManagerCompat = NotificationManagerCompat.from(this)
        return notificationManagerCompat.areNotificationsEnabled()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                notificationPermissionRequestCode
            )
        }
    }
}

@Composable
fun MainLayout(mainViewModel: MainViewModel) {
    val isFabVisible = remember { mutableStateOf(true) }
    val habits = mainViewModel.habits.observeAsState(listOf())
    val showAddOrUpdateAddHabitView = mainViewModel.showAddOrUpdateAddHabitView.observeAsState(false)
    val addOrUpdateHabitRequest = mainViewModel.addOrUpdateHabitData.observeAsState()
    val listNotEmpty = habits.value.isNotEmpty()
    var showSnackbar = remember { mutableStateOf(false) }
    var snackbarMessage = remember { mutableStateOf("") }

    Scaffold(topBar = {
        TopBar()
    }, floatingActionButton = {
        if (listNotEmpty) {
            AnimatedVisibility(visible = isFabVisible.value, enter = fadeIn(), exit = fadeOut()) {
                FloatingActionView(mainViewModel) {
                    mainViewModel.showOrHideAddOrUpdateHabitView(true)
                }
            }
        }
    }, floatingActionButtonPosition = FabPosition.Center) { padding ->
        Box(modifier = Modifier
            .padding(paddingValues = padding)
            .background(MaterialTheme.colorScheme.secondary)
        ) {
            if (listNotEmpty) {
                HabitList(mainViewModel, isFabVisible, showSnackbar = { message ->
                    showSnackbar.value = true
                    snackbarMessage.value = message
                })
                SnackbarWithoutScaffold(
                    message = snackbarMessage.value,
                    showSb = showSnackbar.value,
                    showSnackbar = {showSnackbar.value = it}
                )
            } else {
                EmptyHabitList {
                    mainViewModel.showOrHideAddOrUpdateHabitView(true)
                }
            }

            if (showAddOrUpdateAddHabitView.value) {
                AddOrUpdateHabitSheet(
                    mainViewModel = mainViewModel,
                    onDismiss = {
                        mainViewModel.showOrHideAddOrUpdateHabitView(false)
                        mainViewModel.recordAddOrUpdateHabitRequestInfo(null)
                    },
                    onAddOrUpdateHabitRequest = {
                        addOrUpdateHabitRequest.value?.let {
                            mainViewModel.addOrUpdateHabit(it)
                            mainViewModel.showOrHideAddOrUpdateHabitView(false)
                            mainViewModel.recordAddOrUpdateHabitRequestInfo(null)
                        } },
                    onDeleteHabitRequest = {
                        addOrUpdateHabitRequest.value?.id?.let { mainViewModel.deleteHabit(it) }.also {
                            mainViewModel.showOrHideAddOrUpdateHabitView(false)
                            mainViewModel.recordAddOrUpdateHabitRequestInfo(null)
                        }
                    }
                )
            }
        }
    }
}