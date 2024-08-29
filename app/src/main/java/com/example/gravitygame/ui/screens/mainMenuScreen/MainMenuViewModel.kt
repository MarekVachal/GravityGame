package com.example.gravitygame.ui.screens.mainMenuScreen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainMenuViewModel : ViewModel() {

    private val _mainmenuUiState = MutableStateFlow(MainMenuUiStates())
    val mainMenuUiStates: StateFlow<MainMenuUiStates> = _mainmenuUiState.asStateFlow()

    fun showMenuList(isShow: Boolean){
        if(isShow){
           _mainmenuUiState.value = _mainmenuUiState.value.copy(showMenuList = true)
        } else {
            _mainmenuUiState.value = _mainmenuUiState.value.copy(showMenuList = false)
        }
    }
}