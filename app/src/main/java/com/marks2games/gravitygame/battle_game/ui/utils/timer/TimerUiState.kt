package com.marks2games.gravitygame.battle_game.ui.utils.timer

data class TimerUiState (
    val timer: CoroutineTimer? = null,

    val second: Int? = null,
    val minute: Int? = null,
    val isRunning: Boolean = false
)