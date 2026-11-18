package com.marks2games.gravitygame.battle_game.ui.utils.timer

import com.marks2games.gravitygame.battle_game.domain.turn.BattleClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TurnTimer(private val scope: CoroutineScope) : BattleClock {
    private val _state = MutableStateFlow(TimerUiState())
    val state = _state.asStateFlow()
    private var originalTimeMillis = 0L
    private var timeLeftMillis = 0L
    private var onFinish: (() -> Unit)? = null
    private var timerJob: Job? = null

    override val remainingSeconds: Long
        get() = timeLeftMillis / 1_000

    override fun makeTimer(seconds: Int, onFinish: () -> Unit) {
        stopTimer()
        originalTimeMillis = seconds * 1_000L
        resetTimer()
        this.onFinish = onFinish
        startTimer()
    }

    fun startTimer() {
        val finish = onFinish ?: return
        if (timerJob != null) return

        _state.update { it.copy(isRunning = true) }
        updateTime()
        timerJob = scope.launch(start = CoroutineStart.LAZY) {
            while (timeLeftMillis > 0) {
                timeLeftMillis = (timeLeftMillis - 1_000).coerceAtLeast(0)
                updateTime()
                delay(1_000)
            }
            timerJob = null
            _state.update { it.copy(isRunning = false) }
            finish()
        }
        timerJob?.start()
    }

    override fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        _state.update { it.copy(isRunning = false) }
    }

    override fun cancelTimer() {
        stopTimer()
        onFinish = null
    }

    override fun restartTurn() {
        resetTimer()
        startTimer()
    }

    private fun resetTimer() {
        timeLeftMillis = originalTimeMillis
    }

    fun restartTimer(newTime: Long) {
        timeLeftMillis = newTime
        startTimer()
    }

    private fun updateTime() {
        val seconds = timeLeftMillis / 1_000
        _state.update { it.copy(minute = (seconds / 60).toInt(), second = (seconds % 60).toInt()) }
    }
}
