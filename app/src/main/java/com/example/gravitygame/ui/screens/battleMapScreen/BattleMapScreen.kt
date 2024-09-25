package com.example.gravitygame.ui.screens.battleMapScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.gravitygame.R
import com.example.gravitygame.ai.createAiArmy
import com.example.gravitygame.database.DatabaseViewModel
import com.example.gravitygame.tutorial.Tasks
import com.example.gravitygame.tutorial.TutorialDialog
import com.example.gravitygame.tutorial.TutorialViewModel
import com.example.gravitygame.ui.screens.armyDialogScreen.ArmyDialog
import com.example.gravitygame.timer.CoroutineTimer
import com.example.gravitygame.ui.screens.infoDialogsScreens.EndOfGameDialog
import com.example.gravitygame.ui.screens.infoDialogsScreens.LocationInfoDialog
import com.example.gravitygame.timer.TimerViewModel
import com.example.gravitygame.ui.screens.infoDialogsScreens.BattleInfoDialog
import com.example.gravitygame.ui.screens.settingScreen.SettingViewModel
import com.example.gravitygame.ui.utils.Players
import androidx.compose.runtime.rememberCoroutineScope
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
    endOfGame: () -> Unit


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
        playerData = battleModel.playerData)
    LocationInfoDialog(battleModel = battleModel, toShow = movementUiState.showLocationInfoDialog)
    BattleInfoDialog(battleModel = battleModel, toShow = movementUiState.showBattleInfoOnLocation, location = movementUiState.battleLocationToShow)

    if (!movementUiState.isBattleScreenInitialized) {
        val enemyShipList = battleModel.battleMap?.let { createAiArmy(battleMap = it, startLocation = locationListUiState.locationList.last().id) }
        enemyShipList?.let { battleModel.initializeEnemyShipList(enemyShipList = it) }
        timerModel.makeTimer(
            CoroutineTimer(
                timerModel = timerModel,
                finishTurn = {
                    coroutineScope.launch {
                        battleModel.finishTurn(timerModel = timerModel, databaseModel = databaseModel)
                    }
                },
                secondsForTurn = battleModel.battleMap?.secondsForTurn ?: 0
            )
        )
        battleModel.cleanEnemyRecord()
        battleModel.changeBattleScreenInitialization(isInitialized = true)
    }

    TutorialDialog(tutorialModel = tutorialModel, toShow = tutorialUiState.showTutorialDialog, timerModel = timerModel, settingsModel = settingsModel, context = context)
    if(settingsUiState.showTutorial){
        if(!tutorialUiState.battleOverviewTask){
            tutorialModel.showTutorialDialog(toShow = true, task = Tasks.BATTLE_OVERVIEW, timerModel = timerModel)
        }
        if(!tutorialUiState.movementTask && tutorialUiState.battleOverviewTask){
            tutorialModel.showTutorialDialog(toShow = true, task = Tasks.MOVEMENT, timerModel = timerModel)
        }
        if(!tutorialUiState.locationInfoTask && tutorialUiState.acceptableLostTask && !movementUiState.showArmyDialog){
            tutorialModel.showTutorialDialog(toShow = true, task = Tasks.LOCATION_INFO, timerModel = timerModel)
        }
        if(!tutorialUiState.locationOwnerTask && movementUiState.turn == 2){
            tutorialModel.showTutorialDialog(toShow = true, task = Tasks.LOCATION_OWNER, timerModel = timerModel)
        }
        if(locationListUiState.locationList.any { location -> location.wasBattleHere.value }){
            tutorialModel.showTutorialDialog(toShow = true, task = Tasks.BATTLE_INFO, timerModel = timerModel)
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

    battleModel.battleMap?.MapLayout(
        modifier = modifier,
        battleModel = battleModel,
        record = movementRecord.movementRecordOfTurn,
        enemyRecord = movementRecord.enemyRecord,
        locationList = locationListUiState.locationList
    ) ?: return

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

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End
    ) {
        val baseLocation = if(battleModel.playerData.player == Players.PLAYER1){
            battleModel.battleMap?.player1Base ?: return} else {battleModel.battleMap?.player2Base?: return}
        FloatingActionButton(
            onClick = {
                if(!battleModel.checkShipLimitOnBase(baseLocation)){
                    Toast.makeText(
                        context,
                        context.getString(R.string.manyShipsOnBaseLocation),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    coroutineScope.launch {
                        battleModel.finishTurn(
                            timerModel = timerModel,
                            databaseModel = databaseModel
                        )
                    }
                }
            },
            modifier.padding(end = 16.dp, bottom = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.nextTurn),
                modifier = modifier.padding(16.dp))
        }
    }


    ArmyDialog(
        battleModel = battleModel,
        tutorialModel = tutorialModel,
        show = movementUiState.showArmyDialog,
        timerModel = timerModel,
        settingsModel = settingsModel,
        context = context
    )


}