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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

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
            ).pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    onCheckedChange(!checked)
                })
            },
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