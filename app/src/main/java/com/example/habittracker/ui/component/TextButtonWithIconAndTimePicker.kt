package com.example.habittracker.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.habittracker.ui.theme.HabitTrackerTheme

@Composable
fun TextButtonWithIconAndTimePicker(
    text: String, iconPainter: Painter? = null,
    iconVector: ImageVector? = null,
    selectedValue: String? = null,
    onSelected: (time: String) -> Unit) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var time by remember {
        mutableStateOf(selectedValue ?: "")
    }

    Box {
        if (time.isBlank()) {
            Button(
                onClick = { expanded = true },
                colors = ButtonDefaults.buttonColors(
                    contentColor = LocalContentColor.current
                ),
                contentPadding = PaddingValues(16.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconWithPainterOrVector(iconPainter = iconPainter, iconVector = iconVector)
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleMedium,
                        color = LocalContentColor.current
                    )
                }
            }
        } else {
            AssistChip(
                onClick = { time = "" },
                trailingIcon = {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, Modifier.size(16.dp))
                },
                label = { Text(text = time)},
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    trailingIconContentColor = MaterialTheme.colorScheme.onBackground,
                    labelColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.padding(8.dp)
            )
        }
        TimePicker(
            label = "Select reminder time",
            value = time,
            onValueChange = { value ->
                time = value
                expanded = false
                onSelected(value)
            },
            showDialog = expanded
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TextButtonWithIconAndTimePicker() {
    HabitTrackerTheme {
        AssistChip(
            onClick = {  },
            trailingIcon = {
                Icon(imageVector = Icons.Default.Close, contentDescription = null, Modifier.size(16.dp))
                           },
            label = { Text(text = "remind me at 8")},
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                trailingIconContentColor = MaterialTheme.colorScheme.onBackground,
                labelColor = MaterialTheme.colorScheme.onBackground
            )
        )
    }
}
