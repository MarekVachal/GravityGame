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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SwipeUtil(
    modifier: Modifier = Modifier,
    delete: () -> Unit,
    edit: () -> Unit,
    enableEdit: Boolean,
    content: @Composable RowScope.() -> Unit
){
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    delete()
                    true
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    edit()
                    enableEdit
                }

                SwipeToDismissBoxValue.Settled -> {
                    false
                }
            }
        }
    )

    var icon: ImageVector? = null
    lateinit var alignment: Alignment
    var color: Color = MaterialTheme.colorScheme.secondaryContainer

    when(dismissState.dismissDirection){
        SwipeToDismissBoxValue.StartToEnd -> {
            icon = Icons.Default.Delete
            alignment = Alignment.CenterEnd
            color = MaterialTheme.colorScheme.errorContainer
        }
        SwipeToDismissBoxValue.EndToStart -> {
            icon = Icons.Default.Edit
            alignment = Alignment.CenterStart
            color = Color.Yellow
        }
        SwipeToDismissBoxValue.Settled -> {}
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            if(icon != null){
                Box(
                    contentAlignment = alignment,
                    modifier = modifier
                        .fillMaxSize()
                        .background(color)
                ){
                    Icon(
                        modifier = modifier.minimumInteractiveComponentSize(),
                        imageVector = icon, contentDescription = null
                    )
                }
            }

        },
        enableDismissFromEndToStart = enableEdit,
        content = content
    )
}