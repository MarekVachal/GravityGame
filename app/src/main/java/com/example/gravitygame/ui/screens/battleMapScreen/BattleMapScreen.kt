package com.example.gravitygame.ui.screens.battleMapScreen


import android.content.Context
import android.util.Log
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.example.gravitygame.tutorial.Tasks
import com.example.gravitygame.tutorial.TutorialDialog
import com.example.gravitygame.tutorial.TutorialViewModel
import com.example.gravitygame.ui.screens.armyDialogScreen.ArmyDialog
import com.example.gravitygame.timer.CoroutineTimer
import com.example.gravitygame.ui.screens.infoDialogsScreens.EndOfGameDialog
import com.example.gravitygame.ui.screens.infoDialogsScreens.LocationInfoDialog
import com.example.gravitygame.timer.TimerViewModel
import com.example.gravitygame.ui.screens.settingScreen.SettingViewModel

@Composable
fun BattleMapScreen(
    modifier: Modifier = Modifier,
    battleModel: BattleViewModel,
    timerModel: TimerViewModel,
    tutorialModel: TutorialViewModel,
    settingsModel: SettingViewModel,
    context: Context,
    timer: CoroutineTimer,
    endOfGame: () -> Unit

) {
    val movementUiState by battleModel.movementUiState.collectAsState()
    val timerUiState by timerModel.timerUiState.collectAsState()
    val locationListUiState by battleModel.locationListUiState.collectAsState()
    val movementRecord by battleModel.movementRecord.collectAsState()
    val tutorialUiState by tutorialModel.tutorialUiState.collectAsState()
    val settingsUiState by settingsModel.settingUiState.collectAsState()
    var initialization by rememberSaveable { mutableStateOf(false) }

    EndOfGameDialog(
        toShow = movementUiState.showEndOfGameDialog,
        onDismissRequest = endOfGame,
        confirmButton = endOfGame,
        playerData = battleModel.playerData)
    LocationInfoDialog(battleModel = battleModel, toShow = movementUiState.showLocationInfoDialog)

    if (!initialization) {
        battleModel.battleMap?.let { timer.updateTimerTime(it.secondsForTurn) }

        //AI call
        Log.d("To Battle", "enemyShipList starts initializing")
        val enemyShipList = battleModel.battleMap?.let { createAiArmy(battleMap = it, startLocation = locationListUiState.locationList.last().id) }
        Log.d("To Battle", "enemyShipList variable initialized")
        enemyShipList?.let { battleModel.initializeEnemyShipList(enemyShipList = it) }
        Log.d("To Battle", "enemyShipList initialized")

        Log.d("To Battle", "Timer starts initializing")
        timer.startTimer()
        initialization = true
        Log.d("To Battle", "Timer initialized")
    }

    TutorialDialog(tutorialModel = tutorialModel, toShow = tutorialUiState.showTutorialDialog, timer = timer, settingsModel = settingsModel, context = context)
    if(!tutorialUiState.battleOverviewTask && settingsUiState.showTutorial){
        tutorialModel.showTutorialDialog(toShow = true, task = Tasks.BATTLE_OVERVIEW, timer = timer)
    }
    if(!tutorialUiState.movementTask && tutorialUiState.battleOverviewTask && settingsUiState.showTutorial){
        tutorialModel.showTutorialDialog(toShow = true, task = Tasks.MOVEMENT, timer = timer)
    }
    if(!tutorialUiState.locationInfoTask && tutorialUiState.acceptableLostTask && settingsUiState.showTutorial && !movementUiState.showArmyDialog){
        tutorialModel.showTutorialDialog(toShow = true, task = Tasks.LOCATION_INFO, timer = timer)
    }
    if(!tutorialUiState.locationOwnerTask && movementUiState.turn == 2 && settingsUiState.showTutorial){
        tutorialModel.showTutorialDialog(toShow = true, task = Tasks.LOCATION_OWNER, timer = timer)
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

    Log.d("To Battle", "MapLayout starts initializing")
    battleModel.battleMap?.MapLayout(
        modifier = modifier,
        battleModel = battleModel,
        record = movementRecord.movementRecordOfTurn,
        locationList = locationListUiState.locationList
    ) ?: return println("MapLayout initialization return")
    Log.d("To Battle", "MapLayout initialized")

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
        FloatingActionButton(
            onClick = {
                timer.stopTimer()
                battleModel.finishTurn(timer = timer)
                timer.resetTimer()
                timer.startTimer()
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
        timer = timer,
        settingsModel = settingsModel,
        context = context
    )


}