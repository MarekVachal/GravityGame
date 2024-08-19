package com.example.gravitygame.viewModels

import androidx.lifecycle.ViewModel
import com.example.gravitygame.uiStates.TimerUiState
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
}