package com.marks2games.gravitygame.battle_game.ui.screens.battleMapScreen

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.battle_game.domain.ai.createAiArmy
import com.marks2games.gravitygame.battle_game.data.room_database.DatabaseViewModel
import com.marks2games.gravitygame.battle_game.data.model.enum_class.Tasks
import com.marks2games.gravitygame.battle_game.ui.tutorial.TutorialDialog
import com.marks2games.gravitygame.battle_game.ui.tutorial.TutorialViewModel
import com.marks2games.gravitygame.battle_game.ui.utils.timer.CoroutineTimer
import com.marks2games.gravitygame.battle_game.ui.utils.EndOfGameDialog
import com.marks2games.gravitygame.battle_game.ui.utils.LocationInfoDialog
import com.marks2games.gravitygame.battle_game.ui.utils.timer.TimerViewModel
import com.marks2games.gravitygame.battle_game.ui.utils.BattleInfoDialog
import com.marks2games.gravitygame.ui.screens.settingScreen.SettingViewModel
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.IntOffset
import com.marks2games.gravitygame.battle_game.ui.utils.CapitulateInfoDialog
import com.marks2games.gravitygame.battle_game.ui.screens.selectArmyScreen.SelectArmyViewModel
import com.marks2games.gravitygame.core.ui.utils.ProgressIndicator
import com.marks2games.gravitygame.core.ui.utils.TimerCard
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.internal.wait

@Composable
fun BattleMapScreen(
    modifier: Modifier = Modifier,
    battleModel: BattleViewModel,
    timerModel: TimerViewModel,
    tutorialModel: TutorialViewModel,
    settingsModel: SettingViewModel,
    databaseModel: DatabaseViewModel,
    selectArmyModel: SelectArmyViewModel,
    context: Context,
    endOfGame: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val playerData by battleModel.playerData.collectAsState()
    val movementUiState by battleModel.movementUiState.collectAsState()
    val selectArmyUiState by selectArmyModel.selectArmyUiState.collectAsState()
    val locationListUiState by battleModel.locationListUiState.collectAsState()
    val movementRecord by battleModel.movementRecord.collectAsState()
    val tutorialUiState by tutorialModel.tutorialUiState.collectAsState()
    val settingsUiState by settingsModel.settingUiState.collectAsState()
    val iconPositionX = remember { Animatable(movementUiState.iconPositionX) }
    val iconPositionY = remember { Animatable(movementUiState.iconPositionY) }

    LaunchedEffect(movementUiState.iconPositionX, movementUiState.iconPositionY) {
        launch {
            iconPositionX.animateTo(
                targetValue = movementUiState.iconPositionX,
                animationSpec = tween(100)
            )
        }
        launch {
            iconPositionY.animateTo(
                targetValue = movementUiState.iconPositionY,
                animationSpec = tween(100)
            )
        }
    }

    LaunchedEffect (Unit){
        timerModel.stopTimer()
        battleModel.resetUiStateForNewBattle()
        battleModel.createArmyList(selectArmyUiState = selectArmyUiState)
        if(!playerData.isOnline && locationListUiState.locationList[battleModel.findOpponentBaseLocation()].enemyShipList.isEmpty()){
            val enemyShipList = createAiArmy(battleMap = battleModel.battleMap)
            battleModel.initializeEnemyShipList(enemyShipList = enemyShipList)
            battleModel.showTimer(false)
        }
        if(playerData.isOnline){
            battleModel.initializeBattleGameRepository(playerData).wait()
            battleModel.updateLocations(
                isSetup = true,
                timerModel = timerModel,
                databaseModel = databaseModel
            )
            timerModel.makeTimer(
                CoroutineTimer(
                    timerModel = timerModel,
                    onFinishTimer = {
                        MainScope().launch {
                            battleModel.finishTurn(
                                timerModel = timerModel,
                                databaseModel = databaseModel
                            )
                            timerModel.resetTimer()
                            timerModel.startTimer()
                        }
                    },
                    secondsForTurn = battleModel.battleMap.secondsForTurn
                )
            )
            battleModel.showTimer(true)
        }
        battleModel.cleanEnemyRecord()
    }

    DisposableEffect(Unit) {
        battleModel.listenForCapitulation(
            timerModel,
            databaseModel = databaseModel
        )
        onDispose {  }
    }

    EndOfGameDialog(
        toShow = movementUiState.showEndOfGameDialog,
        onDismissRequest = endOfGame,
        confirmButton = endOfGame,
        dismissButton = { battleModel.showEndOfGameDialog(false) },
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

    TutorialDialog(
        tutorialModel = tutorialModel,
        toShow = tutorialUiState.showTutorialDialog,
        timerModel = timerModel,
        settingsModel = settingsModel
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
    ) {
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

    if (movementUiState.draggingIconVisible && movementUiState.startPosition != null && !movementUiState.endOfGame) {
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        movementUiState.iconPositionX.toInt(),
                        movementUiState.iconPositionY.toInt()
                    )
                }
                .size(battleModel.battleMap.planetSize)
        ){
            Image(
                painter = painterResource(id = R.drawable.fleet_icon),
                contentDescription = "Moving icon",
                modifier = Modifier
                    .size(battleModel.battleMap.planetSize),
                contentScale = ContentScale.Fit
            )
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ){
        Box(
            modifier = modifier
                .fillMaxWidth()
        ) {
            if(movementUiState.showTimer){
                TimerCard(timerModel)
            }

            IconButton(
                onClick = { battleModel.undoAttack() },
                modifier = modifier
                    .padding(top = 16.dp, end = 16.dp)
                    .align(Alignment.CenterEnd),
                enabled = movementRecord.movementRecordOfTurn.isNotEmpty()
            ){
                Icon(
                    painter = painterResource(id = R.drawable.undo),
                    contentDescription = "Undo icon",
                    tint = Color.Unspecified)
            }
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
        settingsModel = settingsModel
    )

    ProgressIndicator(
        toShow = movementUiState.showProgressIndicator,
        type = movementUiState.progressIndicatorType
    )



}