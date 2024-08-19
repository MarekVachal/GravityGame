package com.example.gravitygame.ui.utils

import com.example.gravitygame.viewModels.TimerViewModel
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

    fun updateTimerTime(secondsForTurn: Int){
        timeLeftInMillis = secondsForTurn.times(1000).toLong()
        originalTimeInMillis = timeLeftInMillis
    }

    private fun updateTimer() {
        timeLeftInMillis -= 1000
        timerModel.updateTimeUi(timeLeftInMillis)
    }

    fun startTimer() {
        if (timerJob != null) return  // If already running, do nothing

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