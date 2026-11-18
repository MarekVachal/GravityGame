package com.marks2games.gravitygame.battle_game.ui.utils.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

class TimerViewModel : ViewModel() {
    val turnTimer = TurnTimer(viewModelScope)
    val timerUiState = turnTimer.state

    fun makeTimer(seconds: Int, onFinish: () -> Unit) = turnTimer.makeTimer(seconds, onFinish)

    fun cancelTimer() = turnTimer.cancelTimer()

    fun stopTimer() = turnTimer.stopTimer()

    fun startTimer() = turnTimer.startTimer()

    fun restartTimer(newTime: Long) = turnTimer.restartTimer(newTime)
}
