package com.marks2games.gravitygame.timer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimerViewModel : ViewModel() {

    private val _timerUiState = MutableStateFlow(TimerUiState())
    val timerUiState: StateFlow<TimerUiState> = _timerUiState.asStateFlow()

    fun updateTimeUi(millisUntilFinished: Long){
        _timerUiState.value = _timerUiState.value.copy(
            second = (millisUntilFinished/1000 % 60).toInt(),
            minute =(millisUntilFinished/1000/60).toInt())
    }

    fun makeTimer(timer: CoroutineTimer){
        _timerUiState.value = _timerUiState.value.copy(timer = timer)
        timer.startTimer()
    }

    fun cancelTimer(){
        stopTimer()
        _timerUiState.value = _timerUiState.value.copy(timer = null)
    }

    fun stopTimer(){
        timerUiState.value.timer?.stopTimer()

    }

    fun resetTimer(){
        timerUiState.value.timer?.resetTimer()
    }

    fun startTimer(){
        timerUiState.value.timer?.startTimer()
    }
}