package com.example.gravitygame.ui.screens.infoDialogsScreens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.gravitygame.R
import com.example.gravitygame.ui.utils.PlayerData

@Composable
fun EndOfGameDialog(
    toShow: Boolean,
    playerData: PlayerData,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    confirmButton: () -> Unit
){
    if(toShow){
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                Button(onClick = confirmButton) {
                    Icon(
                        painter = painterResource(id = R.drawable.check),
                        contentDescription = "Check icon"
                    )
                }
            },
            title = {
                Text(
                    text = if (playerData.lost) {
                        stringResource(id = R.string.titleLostGame)
                    } else if (playerData.win) {
                        stringResource(id = R.string.titleWinGame)
                    } else {
                        stringResource(id = R.string.titleDrawGame)
                    }
                )
            },
            text = {
                Text(
                    text = if (playerData.lost) {
                        stringResource(id = R.string.lostGame)
                    } else if (playerData.win) {
                        stringResource(id = R.string.winGame)
                    } else {
                        stringResource(id = R.string.drawGame)
                    },
                    textAlign = TextAlign.Justify
                )
            },
            modifier = modifier

        )
    }
}