package com.marks2games.gravitygame.ui.screens.infoDialogsScreens

import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.ui.screens.battleMapScreen.BattleViewModel

@Composable
fun PlayerInfoDialog(
    modifier: Modifier = Modifier,
    toShow: Boolean,
    onDismissRequest: () -> Unit,
    battleModel: BattleViewModel,
    context: Context
){
    if(toShow){
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                Button(
                    onClick = onDismissRequest
                ) {
                    Text(
                        text = stringResource(id = R.string.ok)
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(id = R.string.playerTitle)
                )
            },
            text = {
                Text(
                    text = stringResource(
                        id = R.string.playerInfoText,
                        battleModel.setPlayerInfoText(context)
                    )
                )
            },
            modifier = modifier
        )
    }
}