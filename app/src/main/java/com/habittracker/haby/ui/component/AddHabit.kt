package com.habittracker.haby.ui.component

import android.view.Gravity
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.habittracker.haby.R
import com.habittracker.haby.ui.model.AddOrUpdateHabitRequest
import com.habittracker.haby.ui.model.UiRepeatType
import com.habittracker.haby.ui.viewmodel.MainViewModel
import kotlinx.coroutines.android.awaitFrame

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddOrUpdateHabitContent(mainViewModel: MainViewModel,
                            onAddOrUpdateHabitRequest: () -> Unit, onDeleteHabitRequest: () -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    val addOrUpdateHabitRequest = mainViewModel.addOrUpdateHabitData.observeAsState()
    val showDeleteOption = addOrUpdateHabitRequest.value != null &&
            addOrUpdateHabitRequest.value?.id != null
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
            OutlinedTextField(
                value = addOrUpdateHabitRequest.value?.name ?: "",
                label = { Text(text = "What's the habit?") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedContainerColor = MaterialTheme.colorScheme.primary,
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    disabledBorderColor = MaterialTheme.colorScheme.onBackground,
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    disabledLabelColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .fillMaxWidth(if(showDeleteOption) 0.6f else 0.8f)
                    .focusRequester(focusRequester),
                onValueChange = {value -> mainViewModel.recordAddOrUpdateHabitRequestInfo(
                    (addOrUpdateHabitRequest.value)?.copy(name = value)
                        ?: AddOrUpdateHabitRequest(name = value))
                })
            IconButton(
                onClick = {onAddOrUpdateHabitRequest()},
                enabled = (addOrUpdateHabitRequest.value != null &&
                        addOrUpdateHabitRequest.value?.name?.isNotBlank() == true),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Icon(painter = painterResource(id = R.drawable.baseline_arrow_upward_black_24dp), null)
            }

            if (showDeleteOption) {
                IconButton(
                    onClick = {onDeleteHabitRequest()},
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .background(
                            color = MaterialTheme.colorScheme.error,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(imageVector = Icons.Default.Delete, null)
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())) {
            TextButtonWithIconAndDropdownMenu(
                text = "Repeat",
                iconPainter = painterResource(id = R.drawable.baseline_repeat_black_24dp),
                options = UiRepeatType.values().map { it.displayName },
                selectedOptionIndex = addOrUpdateHabitRequest.value?.repeatType?.let {
                    UiRepeatType.values()
                        .indexOf(it)
                }
            ) { selectedIndex ->
                mainViewModel.recordAddOrUpdateHabitRequestInfo(
                    (mainViewModel.addOrUpdateHabitData.value ?: AddOrUpdateHabitRequest())
                        .copy(repeatType = UiRepeatType.values()[selectedIndex])
                )
            }
//            TextButtonWithIconAndDropdownMenu(
//                text = "Color",
//                iconPainter = painterResource(id = R.drawable.baseline_palette_black_24dp),
//                options = listOf("Yellow", "Blue", "Green")) {
//            }
            TextButtonWithIconAndTimePicker(
                text = "Remind",
                iconVector = Icons.Default.Notifications,
                selectedValue = addOrUpdateHabitRequest.value?.reminderTime
            ) { time ->
                mainViewModel.recordAddOrUpdateHabitRequestInfo(
                    (mainViewModel.addOrUpdateHabitData.value ?: AddOrUpdateHabitRequest())
                        .copy(reminderTime = time)
                )
            }
        }
    }
    LaunchedEffect(focusRequester) {
        awaitFrame()
        focusRequester.requestFocus()
        keyboard?.show()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrUpdateHabitSheet(
    mainViewModel: MainViewModel,
    onAddOrUpdateHabitRequest: () -> Unit,
    onDeleteHabitRequest: () -> Unit,
    onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
        dialogWindowProvider.window.setGravity(Gravity.BOTTOM)
        AddOrUpdateHabitContent(mainViewModel, onAddOrUpdateHabitRequest, onDeleteHabitRequest)
    }

}