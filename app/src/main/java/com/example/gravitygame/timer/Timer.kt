package com.example.gravitygame.timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CoroutineTimer(
    private val timerModel: TimerViewModel,
    private val finishTurn: () -> Unit
) {
    private var timeLeftInMillis: Long = 0
    private var originalTimeInMillis: Long = 0
    private var timerJob: Job? = null
    private var isPaused = false

    fun updateTimerTime(secondsForTurn: Int){
        timeLeftInMillis = secondsForTurn.times(1000).toLong()
        originalTimeInMillis = timeLeftInMillis
        timerModel.updateTimeUi(timeLeftInMillis)
    }

    private fun updateTimer() {
        timeLeftInMillis -= 1000
        timerModel.updateTimeUi(timeLeftInMillis)
    }

    fun startTimer() {
        if (timerJob != null || isPaused) return

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
        timerModel.updateTimeUi(timeLeftInMillis)
    }

    fun resetTimer() {
        timeLeftInMillis = originalTimeInMillis
    }

    fun pauseTimer() {
        isPaused = true
        stopTimer()
        timerModel.updateTimeUi(timeLeftInMillis)
    }

    // Continues the timer from where it left off
    fun continueTimer() {
        if (!isPaused || timeLeftInMillis <= 0) return

        isPaused = false
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
}