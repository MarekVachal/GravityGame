package com.example.gravitygame.viewModels

import androidx.lifecycle.ViewModel
import com.example.gravitygame.uiStates.ProgressIndicatorUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProgressIndicatorViewModel: ViewModel() {
    private val _progressIndicatorUiState = MutableStateFlow(ProgressIndicatorUiState())
    val progressIndicatorUiState: StateFlow<ProgressIndicatorUiState> = _progressIndicatorUiState.asStateFlow()

    fun showProgressIndicator(toShow: Boolean){
        if (toShow) {
            _progressIndicatorUiState.value = _progressIndicatorUiState.value.copy(showProgressIndicator = true)
        } else {
            _progressIndicatorUiState.value = _progressIndicatorUiState.value.copy(showProgressIndicator = false)
        }
    }
}

