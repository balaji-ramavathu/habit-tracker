package com.habittracker.haby.ui.component
import android.app.TimePickerDialog
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun TimePicker(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    pattern: String = "HH:mm",
    is24HourView: Boolean = true,
    showDialog: Boolean
) {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val time = if (value.isNotBlank()) LocalTime.parse(value, formatter) else LocalTime.now()
    val dialog = TimePickerDialog(
        LocalContext.current,
        { _, hour, minute -> onValueChange(LocalTime.of(hour, minute).toString()) },
        time.hour,
        time.minute,
        is24HourView,
    )
    if (showDialog) {
        dialog.show()
    } else {
        dialog.hide()
    }
}