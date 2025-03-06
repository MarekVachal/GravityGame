package com.marks2games.gravitygame.core.ui.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marks2games.gravitygame.battle_game.ui.utils.timer.TimerViewModel
import java.util.Locale
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun TimerCard(timerModel: TimerViewModel) {
    val timerUiState by timerModel.timerUiState.collectAsState()

    Card(modifier = Modifier.width(58.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = String.format(
                    Locale.US, "%01d:%02d", timerUiState.minute ?: 0, timerUiState.second ?: 0
                )
            )
        }
    }
}