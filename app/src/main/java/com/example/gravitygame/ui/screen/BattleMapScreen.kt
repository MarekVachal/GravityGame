package com.example.gravitygame.ui.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gravitygame.R
import com.example.gravitygame.ui.utils.CoroutineTimer
import com.example.gravitygame.ui.utils.EndOfGameDialog
import com.example.gravitygame.ui.utils.Players
import com.example.gravitygame.viewModels.BattleViewModel
import com.example.gravitygame.viewModels.ProgressIndicatorViewModel
import com.example.gravitygame.viewModels.TimerViewModel

@Composable
fun BattleMapScreen(
    modifier: Modifier = Modifier,
    battleModel: BattleViewModel,
    timerModel: TimerViewModel,
    progressIndicatorModel: ProgressIndicatorViewModel,
    timer: CoroutineTimer,
    endOfGame: () -> Unit

) {
    val movementUiState by battleModel.movementUiState.collectAsState()
    val timerUiState by timerModel.timerUiState.collectAsState()
    val locationListUiState by battleModel.locationListUiState.collectAsState()
    val movementRecord by battleModel.movementRecord.collectAsState()
    var initialization by rememberSaveable { mutableStateOf(false) }
    EndOfGameDialog(
        toShow = movementUiState.showEndOfGameDialog,
        onDismissRequest = endOfGame,
        confirmButton = endOfGame,
        playerData = battleModel.playerData)


    if (!initialization) {
        battleModel.battleMap?.let { timer.updateTimerTime(it.secondsForTurn) }
        progressIndicatorModel.showProgressIndicator(false)
        timer.startTimer()
        locationListUiState.locationList[0].owner.value = Players.PLAYER1
        locationListUiState.locationList.last().owner.value = Players.PLAYER2
        initialization = true
    }

    Box(
        modifier = modifier.fillMaxSize()
    )
    {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Battle map background",
            contentScale = ContentScale.FillBounds,
            modifier = modifier.matchParentSize()
        )
    }

    battleModel.battleMap?.MapLayout(
        modifier = modifier,
        battleModel = battleModel,
        locationList = locationListUiState.locationList
    ) ?: return /* TODO Something went wrong with the battle map */

    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Card (
            modifier = modifier.padding(top = 16.dp, start = 16.dp)
        ){
            Text(
                modifier = modifier.padding(4.dp),
                text = "${timerUiState.minute}:${timerUiState.second}",
            )
        }

        Button(
            onClick = { battleModel.undoAttack() },
            modifier = modifier.padding(top = 16.dp, end = 16.dp),
            enabled = movementRecord.movementRecordOfTurn.isNotEmpty()
        ) {
            Text(
                text = stringResource(id = R.string.undo)
            )
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
        show = movementUiState.showArmyDialog
    )


}