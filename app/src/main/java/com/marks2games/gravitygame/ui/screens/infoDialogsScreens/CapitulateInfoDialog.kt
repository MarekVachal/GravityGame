package com.marks2games.gravitygame.ui.screens.infoDialogsScreens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.marks2games.gravitygame.R

@Composable
fun CapitulateInfoDialog(
    modifier: Modifier = Modifier,
    toShow: Boolean,
    onCapitulateButtonClick: () -> Unit,
    onDismissRequest: () -> Unit
){
    if(toShow){
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                Button(
                    onClick = onCapitulateButtonClick
                ) {
                    Text(
                        text = stringResource(id = R.string.capitulate)
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismissRequest
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.close),
                        contentDescription = "Close icon"
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(id = R.string.capitulate)
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.capitulateInfoText)
                )
            },
            modifier = modifier
        )
    }
}