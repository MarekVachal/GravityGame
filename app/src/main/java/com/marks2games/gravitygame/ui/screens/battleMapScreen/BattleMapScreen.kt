package com.marks2games.gravitygame.ui.screens.battleMapScreen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import java.util.Locale
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.ai.createAiArmy
import com.marks2games.gravitygame.database.DatabaseViewModel
import com.marks2games.gravitygame.tutorial.Tasks
import com.marks2games.gravitygame.tutorial.TutorialDialog
import com.marks2games.gravitygame.tutorial.TutorialViewModel
import com.marks2games.gravitygame.ui.screens.armyDialogScreen.ArmyDialog
import com.marks2games.gravitygame.timer.CoroutineTimer
import com.marks2games.gravitygame.ui.screens.infoDialogsScreens.EndOfGameDialog
import com.marks2games.gravitygame.ui.screens.infoDialogsScreens.LocationInfoDialog
import com.marks2games.gravitygame.timer.TimerViewModel
import com.marks2games.gravitygame.ui.screens.infoDialogsScreens.BattleInfoDialog
import com.marks2games.gravitygame.ui.screens.settingScreen.SettingViewModel
import androidx.compose.runtime.rememberCoroutineScope
import com.marks2games.gravitygame.ui.screens.infoDialogsScreens.CapitulateInfoDialog
import com.marks2games.gravitygame.ui.utils.ProgressIndicator
import kotlinx.coroutines.launch

@Composable
fun BattleMapScreen(
    modifier: Modifier = Modifier,
    battleModel: BattleViewModel,
    timerModel: TimerViewModel,
    tutorialModel: TutorialViewModel,
    settingsModel: SettingViewModel,
    databaseModel: DatabaseViewModel,
    context: Context,
    endOfGame: () -> Unit,
    showBattleResultMap: () -> Unit


) {
    val coroutineScope = rememberCoroutineScope()
    val movementUiState by battleModel.movementUiState.collectAsState()
    val timerUiState by timerModel.timerUiState.collectAsState()
    val locationListUiState by battleModel.locationListUiState.collectAsState()
    val movementRecord by battleModel.movementRecord.collectAsState()
    val tutorialUiState by tutorialModel.tutorialUiState.collectAsState()
    val settingsUiState by settingsModel.settingUiState.collectAsState()

    EndOfGameDialog(
        toShow = movementUiState.showEndOfGameDialog,
        onDismissRequest = endOfGame,
        confirmButton = endOfGame,
        dismissButton = showBattleResultMap,
        battleModel = battleModel,
        context = context
    )

    CapitulateInfoDialog(
        toShow = movementUiState.showCapitulateInfoDialog,
        onCapitulateButtonClick = {
            battleModel.capitulate(timerModel = timerModel, databaseModel = databaseModel)
            endOfGame()
        },
        onDismissRequest = { battleModel.showCapitulateInfoDialog(toShow = false) }
    )

    LocationInfoDialog(
        battleModel = battleModel,
        toShow = movementUiState.showLocationInfoDialog
    )

    BattleInfoDialog(
        battleModel = battleModel,
        toShow = movementUiState.showBattleInfoOnLocation,
        location = movementUiState.indexOfBattleLocationToShow,
        context = context
    )

    LaunchedEffect (Unit){
        battleModel.cleanAfterCapitulate()
        if(!battleModel.playerData.isOnline && locationListUiState.locationList[battleModel.findOpponentBaseLocation()].enemyShipList.isEmpty()){
            val enemyShipList = createAiArmy(battleMap = battleModel.battleMap)
            battleModel.initializeEnemyShipList(enemyShipList = enemyShipList)
        }
        timerModel.stopTimer()
        timerModel.makeTimer(
            CoroutineTimer(
                timerModel = timerModel,
                finishTurn = {
                    coroutineScope.launch {
                        battleModel.finishTurn(timerModel = timerModel, databaseModel = databaseModel)
                    }
                },
                secondsForTurn = battleModel.battleMap.secondsForTurn
            )
        )
        battleModel.cleanEnemyRecord()
    }

    TutorialDialog(
        tutorialModel = tutorialModel,
        toShow = tutorialUiState.showTutorialDialog,
        timerModel = timerModel,
        settingsModel = settingsModel,
        context = context
    )
    if(settingsUiState.showTutorial){
        if(!tutorialUiState.battleOverviewTask){
            tutorialModel.showTutorialDialog(
                toShow = true,
                task = Tasks.BATTLE_OVERVIEW,
                timerModel = timerModel
            )
        }
        if(!tutorialUiState.movementTask && tutorialUiState.battleOverviewTask){
            tutorialModel.showTutorialDialog(
                toShow = true,
                task = Tasks.MOVEMENT,
                timerModel = timerModel
            )
        }
        if(!tutorialUiState.locationInfoTask && tutorialUiState.acceptableLostTask && !movementUiState.showArmyDialog){
            tutorialModel.showTutorialDialog(
                toShow = true,
                task = Tasks.LOCATION_INFO,
                timerModel = timerModel
            )
        }
        if(!tutorialUiState.locationOwnerTask && movementUiState.turn == 2){
            tutorialModel.showTutorialDialog(
                toShow = true,
                task = Tasks.LOCATION_OWNER,
                timerModel = timerModel
            )
        }
        if(!tutorialUiState.battleInfoTask && locationListUiState.locationList.any { location -> location.wasBattleHere.value }){
            tutorialModel.showTutorialDialog(
                toShow = true,
                task = Tasks.BATTLE_INFO,
                timerModel = timerModel
            )
        }
    }


    Box(
        modifier = modifier.fillMaxSize()
    )
    {
        Image(
            painter = painterResource(id = R.drawable.battle_background),
            contentDescription = "Battle map background",
            contentScale = ContentScale.FillBounds,
            modifier = modifier.matchParentSize()
        )
    }

    battleModel.battleMap.MapLayout(
        modifier = modifier,
        battleModel = battleModel,
        record = movementRecord.movementRecordOfTurn,
        enemyRecord = movementRecord.enemyRecord,
        locationList = locationListUiState.locationList
    )

    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Card (
            modifier = modifier.padding(top = 16.dp, start = 16.dp)
        ){
            Text(
                modifier = modifier.padding(4.dp),
                text = "${timerUiState.minute ?: 0}:${
                    timerUiState.second?.let {String.format(Locale.US, "%02d", it)} ?: "00"
                }",
            )
        }

        IconButton(
            onClick = { battleModel.undoAttack() },
            modifier = modifier.padding(top = 16.dp, end = 16.dp),
            enabled = movementRecord.movementRecordOfTurn.isNotEmpty()
        ){
            Icon(
                painter = painterResource(id = R.drawable.undo),
                contentDescription = "Undo icon",
                tint = Color.Unspecified)
        }
    }

    if(!movementUiState.showProgressIndicator){
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 16.dp, bottom = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if(!movementUiState.endOfGame)Arrangement.SpaceBetween else Arrangement.End
        ) {
            if(!movementUiState.endOfGame){
                Button(
                    onClick = { battleModel.showCapitulateInfoDialog(toShow = true) }
                ) {
                    Text(
                        text = stringResource(id = R.string.capitulate)
                    )
                }
            }

            FloatingActionButton(
                modifier = modifier,
                onClick = {
                    coroutineScope.launch {
                        battleModel.setOnClickButtonNextTurn(
                            context = context,
                            timerModel = timerModel,
                            databaseModel = databaseModel,
                            navigateToMainMenuScreen = endOfGame
                        )
                    }
                }
            ) {
                Text(
                    text = battleModel.setOnClickButtonNextTurnText(context = context),
                    modifier = modifier.padding(16.dp)
                )
            }
        }
    }

    ArmyDialog(
        battleModel = battleModel,
        tutorialModel = tutorialModel,
        toShow = movementUiState.showArmyDialog,
        timerModel = timerModel,
        settingsModel = settingsModel,
        context = context
    )

    ProgressIndicator(
        toShow = movementUiState.showProgressIndicator,
        type = movementUiState.progressIndicatorType
    )



}