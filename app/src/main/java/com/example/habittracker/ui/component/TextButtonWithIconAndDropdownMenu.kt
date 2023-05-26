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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
fun TextButtonWithIconAndDropdownMenu(
    text: String, iconPainter: Painter? = null,
    iconVector: ImageVector? = null,
    options: List<String>,
    selectedOptionIndex: Int? = null,
    onSelected: (selectedIndex: Int) -> Unit) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var selectedIndex by remember {
        mutableStateOf(selectedOptionIndex)
    }

    Box {
        if (selectedIndex == null) {
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
                onClick = { selectedIndex = null },
                trailingIcon = {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, Modifier.size(16.dp))
                },
                label = { Text(text = options[selectedIndex!!])},
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    trailingIconContentColor = MaterialTheme.colorScheme.onBackground,
                    labelColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.padding(8.dp)
            )
        }

        if (expanded) {
            DropdownMenu(expanded = true,
                onDismissRequest = { /*TODO*/ }) {
                options.forEachIndexed {index, option ->
                    DropdownMenuItem(
                        text = { Text(text = option)},
                        onClick = {
                            selectedIndex = index
                            expanded = false
                            onSelected(selectedIndex!!)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun IconWithPainterOrVector(iconPainter: Painter? = null, iconVector: ImageVector? = null) {
    if (iconPainter != null) {
        Icon(
            painter = iconPainter,
            contentDescription = null,
            tint = LocalContentColor.current
        )
    }
    else if (iconVector != null) {
        Icon(
            imageVector = iconVector,
            contentDescription = null,
            tint = LocalContentColor.current
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TextButtonWithIconAndDropdownMenuPreview() {
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
