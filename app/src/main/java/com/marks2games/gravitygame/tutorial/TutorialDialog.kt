package com.marks2games.gravitygame.tutorial

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.models.Tasks
import com.marks2games.gravitygame.timer.TimerViewModel
import com.marks2games.gravitygame.ui.screens.settingScreen.SettingViewModel

@Composable
fun TutorialDialog(
    modifier: Modifier = Modifier,
    tutorialModel: TutorialViewModel,
    timerModel: TimerViewModel?,
    settingsModel: SettingViewModel,
    toShow: Boolean
){
    val tutorialUiState by tutorialModel.tutorialUiState.collectAsState()

    if(toShow){
        AlertDialog(
            onDismissRequest = {
                tutorialModel.showTutorialDialog(
                    toShow = false,
                    timerModel = timerModel
                )
            },
            dismissButton = {
                Button(
                    onClick = {
                        settingsModel.changeShowTutorial(toShow = false)
                        tutorialModel.showTutorialDialog(
                            toShow = false,
                            timerModel = timerModel
                        )
                        tutorialModel.cleanTutorialState()
                    }
                ){
                    Text(text = stringResource(id = R.string.closeTutorial))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        tutorialModel.showTutorialDialog(
                            toShow = false,
                            timerModel = timerModel
                        )
                    }
                ){
                    Icon(painter = painterResource(id = R.drawable.check), contentDescription = "Check icon")
                }
            },
            title = {
                Text(
                    text = stringResource(
                        id = when(tutorialUiState.typeTaskToShow){
                            Tasks.INFO_SHIP -> R.string.infoShipTaskTitle
                            Tasks.NUMBER_SHIPS -> R.string.numberOfShipsTaskTitle
                            Tasks.TIMER -> R.string.timerTaskTitle
                            Tasks.MOVEMENT -> R.string.movementTaskTitle
                            Tasks.LOCATION_INFO -> R.string.locationInfoTitle
                            Tasks.LOCATION_OWNER -> R.string.locationOwnerTitle
                            Tasks.SEND_SHIPS -> R.string.armyDialogSendShipTitle
                            Tasks.ACCEPTABLE_LOST -> R.string.acceptableLossesTitle
                            null -> R.string.unknown
                            Tasks.BATTLE_OVERVIEW -> R.string.battleOverviewTitle
                            Tasks.BATTLE_INFO -> R.string.battleInfoTaskTitle
                        }
                    ),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())){
                    Text(
                        text = stringResource(
                            id = when(tutorialUiState.typeTaskToShow){
                                Tasks.INFO_SHIP -> R.string.infoShipTask
                                Tasks.NUMBER_SHIPS -> R.string.numberShipsTask
                                Tasks.TIMER -> TODO()
                                Tasks.MOVEMENT -> R.string.movementTask
                                Tasks.LOCATION_INFO -> R.string.locationInfoTask
                                Tasks.LOCATION_OWNER -> R.string.locationOwnerTask
                                Tasks.SEND_SHIPS -> R.string.sendShipsTask
                                Tasks.ACCEPTABLE_LOST -> R.string.acceptableLossesTask
                                null -> R.string.unknown
                                Tasks.BATTLE_OVERVIEW -> R.string.battleOverviewTask
                                Tasks.BATTLE_INFO -> R.string.battleInfoTask
                            }
                        ),
                        textAlign = TextAlign.Justify,
                        modifier = modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
}