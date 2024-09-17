package com.example.gravitygame.timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CoroutineTimer(
    private val timerModel: TimerViewModel,
    private val finishTurn: suspend () -> Unit,
    secondsForTurn: Int
) {
    private var timeLeftInMillis: Long = secondsForTurn.times(1000).toLong()
    private var originalTimeInMillis: Long = timeLeftInMillis
    private var timerJob: Job? = null

    private fun updateTimer() {
        timeLeftInMillis -= 1000
        timerModel.updateTimeUi(timeLeftInMillis)
    }

    fun startTimer() {
        if (timerJob != null) return
        
        timerModel.updateTimeUi(timeLeftInMillis)
        timerJob = CoroutineScope(Dispatchers.Main).launch {
            while (timeLeftInMillis > 0) {
                updateTimer()
                delay(1000L)
            }
            stopTimer()
            finishTurn()
            resetTimer()
            startTimer()
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun resetTimer() {
        timeLeftInMillis = originalTimeInMillis
    }

}