package com.marks2games.gravitygame.timer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TimerViewModel : ViewModel() {

    private val _timerUiState = MutableStateFlow(TimerUiState())
    val timerUiState: StateFlow<TimerUiState> = _timerUiState.asStateFlow()

    fun updateTimeUi(millisUntilFinished: Long){
        _timerUiState.value = _timerUiState.value.copy(
            second = (millisUntilFinished/1000 % 60).toInt(),
            minute =(millisUntilFinished/1000/60).toInt())
    }

    fun makeTimer(timer: CoroutineTimer){
        _timerUiState.update { state ->
            state.copy(
                timer = timer,
                isRunning = true
            )
        }
        timer.startTimer()
    }

    fun cancelTimer(){
        stopTimer()
        _timerUiState.update { state ->
            state.copy(
                timer = null,
                isRunning = false
            )
        }
    }

    fun stopTimer(){
        timerUiState.value.timer?.stopTimer()
        _timerUiState.update { state ->
            state.copy(isRunning = false)
        }
    }

    fun resetTimer(){
        timerUiState.value.timer?.resetTimer()
    }

    fun startTimer(){
        timerUiState.value.timer?.startTimer()
        _timerUiState.update { state->
            state.copy(isRunning = true)
        }
    }
}