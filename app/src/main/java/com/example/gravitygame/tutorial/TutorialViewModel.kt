package com.example.gravitygame.tutorial

import androidx.lifecycle.ViewModel
import com.example.gravitygame.ui.utils.CoroutineTimer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TutorialViewModel: ViewModel() {

    private val _tutorialUiState = MutableStateFlow(TutorialUiState())
    val tutorialUiState: StateFlow<TutorialUiState> = _tutorialUiState.asStateFlow()

    fun closeTutorial(){
        _tutorialUiState.value = _tutorialUiState.value.copy(showTutorial = false)
    }

    fun showTutorialDialog(toShow: Boolean, task: Tasks? = null, timer: CoroutineTimer? = null){
        if(toShow){
            _tutorialUiState.value = _tutorialUiState.value.copy(
                showTutorialDialog = true,
                typeTaskToShow = task)
            timer?.pauseTimer()

        } else {
            _tutorialUiState.value = _tutorialUiState.value.copy(
                showTutorialDialog = false)
            tutorialUiState.value.typeTaskToShow?.let { closeTask(task = it) } ?: return
            _tutorialUiState.value = _tutorialUiState.value.copy(
                typeTaskToShow = task
            )
            timer?.continueTimer()
        }
    }

    private fun closeTask(task: Tasks){
        when(task){
            Tasks.INFO_SHIP -> _tutorialUiState.value = _tutorialUiState.value.copy(infoShipTask = true)
            Tasks.NUMBER_SHIPS -> _tutorialUiState.value = _tutorialUiState.value.copy(numberShipsTask = true)
            Tasks.TIMER -> _tutorialUiState.value = _tutorialUiState.value.copy(timerTask = true)
            Tasks.MOVEMENT -> _tutorialUiState.value = _tutorialUiState.value.copy(movementTask = true)
            Tasks.LOCATION_INFO -> _tutorialUiState.value = _tutorialUiState.value.copy(locationInfoTask = true)
            Tasks.LOCATION_OWNER -> _tutorialUiState.value = _tutorialUiState.value.copy(locationOwnerTask = true)
            Tasks.SEND_SHIPS -> _tutorialUiState.value = _tutorialUiState.value.copy(sendShipsTask = true)
            Tasks.ACCEPTABLE_LOST -> _tutorialUiState.value = _tutorialUiState.value.copy(acceptableLostTask = true)
            Tasks.BATTLE_OVERVIEW -> _tutorialUiState.value = _tutorialUiState.value.copy(battleOverviewTask = true)
        }
    }
}