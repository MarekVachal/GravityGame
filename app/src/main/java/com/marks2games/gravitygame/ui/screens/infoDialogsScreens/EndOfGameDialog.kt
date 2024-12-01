package com.marks2games.gravitygame.ui.screens.infoDialogsScreens

import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.ui.screens.battleMapScreen.BattleViewModel

@Composable
fun EndOfGameDialog(
    toShow: Boolean,
    modifier: Modifier = Modifier,
    battleModel: BattleViewModel,
    context: Context,
    onDismissRequest: () -> Unit,
    confirmButton: () -> Unit,
    dismissButton: () -> Unit
){
    if(toShow){
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                Button(onClick = confirmButton) {
                    Text(
                        text = stringResource(R.string.exit)
                    )
                }
            },
            dismissButton = {
                Button(onClick = dismissButton) {
                    Text(
                        text = stringResource(R.string.showMap)
                    )
                }
            },
            title = {
                Text(
                    text = battleModel.setEndOfGameText(isTitle = true, context = context),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = battleModel.setEndOfGameText(isTitle = false, context = context),
                    textAlign = TextAlign.Justify
                )
            },
            modifier = modifier

        )
    }
}