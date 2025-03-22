package com.marks2games.gravitygame.battle_game.ui.screens.matchmakingScreen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.battle_game.ui.utils.timer.TimerViewModel
import com.marks2games.gravitygame.core.ui.utils.SignInDialog
import com.marks2games.gravitygame.core.ui.utils.ProgressIndicator
import com.marks2games.gravitygame.core.data.model.enum_class.ProgressIndicatorType
import java.util.Locale

@Composable
fun MatchmakingScreen(
    modifier: Modifier = Modifier,
    matchmakingModel: MatchmakingViewModel,
    timerModel: TimerViewModel,
    onMatchConfirmed: () -> Unit,
    onBackMainMenuScreen: () -> Unit,
    context: Context,
    roomId: String?
){
    val matchmakingUiStates by matchmakingModel.matchmakingUiStates.collectAsState()
    val timerUiState by timerModel.timerUiState.collectAsState()

    LaunchedEffect(roomId) {
        Log.d("FCM", "Room id in MatchmakingScreen: $roomId")
        if(matchmakingModel.getRoomRef() == null){
            Log.d("FCM", "Room ref MatchmakingScreen: ${matchmakingModel.getRoomRef()}")
            if(!roomId.isNullOrEmpty()){
                matchmakingModel.restoreGameSession(roomId)
                matchmakingModel.handleRoomStateAfterNotification(context)
            }
        }
    }

    LaunchedEffect (Unit) {
        if(roomId.isNullOrEmpty()) {
            matchmakingModel.showProgressIndicator(toShow = true)
            matchmakingModel.opponentFound(isFound = false)
            matchmakingModel.waitingForConfirmation(false)
            matchmakingModel.showSignInDialog(false)
            matchmakingModel.startMatchmaking(context = context)
            timerModel.cancelTimer()
        }
    }

    SignInDialog(
        modifier = modifier,
        toShow = matchmakingUiStates.toShowSignInDialog,
        backToMainMenu = onBackMainMenuScreen,
        signInAnonymously = { matchmakingModel.signInAnonymously() },
        signInWithGoogle = {
            matchmakingModel.signInWithGoogle(context = context)
        }
    )

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

    ProgressIndicator(
        modifier = modifier,
        toShow = matchmakingUiStates.toShow,
        type = ProgressIndicatorType.WAITING_FOR_OPPONENT
    )

    if(timerUiState.isRunning){
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                modifier = modifier.padding(top = 16.dp, start = 16.dp)
            ) {
                Text(
                    modifier = modifier.padding(4.dp),
                    text = "${timerUiState.minute ?: 0}:${
                        timerUiState.second?.let { String.format(Locale.US, "%02d", it) } ?: "00"
                    }",
                )
            }
        }
    }

    if(matchmakingUiStates.opponentFound && !matchmakingUiStates.toShow){
        OutlinedButton(
            onClick = {
                matchmakingModel.confirmPresence(
                    onMatchConfirmed = onMatchConfirmed,
                    timerModel = timerModel,
                    context = context
                )
            },
            modifier = modifier
                .fillMaxSize()
                .wrapContentSize(),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.opponentFound),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }

    if(matchmakingUiStates.waitingListener != null){
        Row (
            modifier = modifier
                .fillMaxSize()
                .padding(end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom,

        ){
            Button(
                onClick = {
                    matchmakingModel.cancelMatchmaking()
                    onBackMainMenuScreen()
                }
            ) {
                Text(
                    text = stringResource(R.string.cancel)
                )
            }
        }

    }
}