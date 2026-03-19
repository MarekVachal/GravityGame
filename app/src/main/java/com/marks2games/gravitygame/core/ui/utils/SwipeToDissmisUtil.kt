package com.marks2games.gravitygame.core.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

@Composable
fun SwipeUtil(
    modifier: Modifier = Modifier,
    delete: () -> Unit,
    edit: () -> Unit,
    enableEdit: Boolean,
    content: @Composable RowScope.() -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()
    val scope = rememberCoroutineScope()

    val (icon, alignment, color) = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Triple(
            Icons.Default.Delete,
            Alignment.CenterStart,
            MaterialTheme.colorScheme.errorContainer
        )

        SwipeToDismissBoxValue.EndToStart -> Triple(
            Icons.Default.Edit,
            Alignment.CenterEnd,
            Color.Yellow
        )

        SwipeToDismissBoxValue.Settled -> Triple(null, Alignment.Center, Color.Transparent)
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromEndToStart = enableEdit,
        onDismiss = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    delete()
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    if (enableEdit) edit()
                    scope.launch { dismissState.reset() }
                }

                SwipeToDismissBoxValue.Settled -> Unit
            }
        },
        backgroundContent = {
            if (icon != null) {
                Box(
                    contentAlignment = alignment,
                    modifier = modifier
                        .fillMaxSize()
                        .background(color)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.minimumInteractiveComponentSize()
                    )
                }
            }
        },
        content = content
    )
}