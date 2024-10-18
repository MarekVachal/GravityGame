package com.example.gravitygame.ui.screens.infoDialogsScreens

import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.gravitygame.R
import com.example.gravitygame.ui.screens.battleMapScreen.BattleViewModel

@Composable
fun EndOfGameDialog(
    toShow: Boolean,
    modifier: Modifier = Modifier,
    battleModel: BattleViewModel,
    context: Context,
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