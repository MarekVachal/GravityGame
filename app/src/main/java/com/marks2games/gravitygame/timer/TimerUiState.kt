package com.marks2games.gravitygame.timer

data class TimerUiState (
    val timer: CoroutineTimer? = null,

    val second: Int? = null,
    val minute: Int? = null,
    val isRunning: Boolean = false
)