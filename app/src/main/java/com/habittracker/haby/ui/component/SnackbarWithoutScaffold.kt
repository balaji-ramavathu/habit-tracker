package com.habittracker.haby.ui.component


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun SnackbarWithoutScaffold(
    message: String,
    showSb: Boolean,
    showSnackbar: (Boolean) -> Unit
) {

    val snackState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()

    SnackbarHost(
        hostState = snackState
    ){
        Snackbar(
            snackbarData = it,
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = Color.Black,
            shape = RoundedCornerShape(8.dp)
        )
    }


    if (showSb){
        LaunchedEffect(Unit) {
            snackScope.launch { snackState.showSnackbar(
                message,
                null,
                false,
                SnackbarDuration.Short
            ) }
            showSnackbar(false)
        }

    }


}