package com.example.gravitygame.ui.screens.mainMenuScreen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gravitygame.R

enum class Text{
    ABOUT_US,
    GAME_RULES,
    ABOUT_GAME,
    DONATE
}

@Composable
fun InfoTextDialog(
    mainMenuModel: MainMenuViewModel,
    mainMenuUiStates: MainMenuUiStates,
    modifier: Modifier = Modifier,
    toShow: Boolean,
    context: Context
){
    if(toShow){
        Dialog(
            onDismissRequest = { mainMenuModel.openTextDialog(toShow = false) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Row (
                modifier = modifier
                    .fillMaxWidth(0.7F)
                    .fillMaxHeight()
            ){
                Card (
                    modifier = modifier
                        .fillMaxSize(),
                    shape = RoundedCornerShape(16.dp)
                ){
                    Row (
                        modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ){
                        Text(
                            text = when(mainMenuUiStates.textToShow){
                                Text.ABOUT_US -> stringResource(id = R.string.aboutUsTitle)
                                null -> stringResource(id = R.string.unknown)
                                Text.GAME_RULES -> stringResource(id = R.string.gameRulesTitle)
                                Text.ABOUT_GAME -> stringResource(id = R.string.aboutGameTitle)
                                Text.DONATE -> stringResource(id = R.string.donateTitle)
                            },
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Row(
                        modifier = modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ){
                        Text(
                            text = when(mainMenuUiStates.textToShow){
                                Text.ABOUT_US -> stringResource(id = R.string.aboutUsText)
                                null -> stringResource(id = R.string.unknown)
                                Text.GAME_RULES -> stringResource(id = R.string.gameRulesText)
                                Text.ABOUT_GAME -> stringResource(id = R.string.aboutGameText)
                                Text.DONATE -> stringResource(id = R.string.donateText)
                            },
                            textAlign = TextAlign.Justify
                        )
                    }
                    if(mainMenuUiStates.textToShow == Text.DONATE){
                        Row (
                            modifier = modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ){
                            Button(
                                onClick = { mainMenuModel.openBuyMeACoffeeLink(context = context) },
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.bmc_button),
                                    contentDescription = "Icon Buy me a coffee"
                                )

                            }
                        }
                    }
                }
            }
        }
    }


}