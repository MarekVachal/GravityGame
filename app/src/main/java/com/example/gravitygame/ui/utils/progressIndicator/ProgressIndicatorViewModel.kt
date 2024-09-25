package com.example.gravitygame.ui.utils.progressIndicator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Suppress("Unused")
class ProgressIndicatorViewModel: ViewModel() {
    private val _progressIndicatorUiState = MutableStateFlow(ProgressIndicatorUiState())
    val progressIndicatorUiState: StateFlow<ProgressIndicatorUiState> = _progressIndicatorUiState.asStateFlow()

    fun showProgressIndicator(toShow: Boolean){
        _progressIndicatorUiState.value = _progressIndicatorUiState.value.copy(showProgressIndicator = toShow)
    }
}

