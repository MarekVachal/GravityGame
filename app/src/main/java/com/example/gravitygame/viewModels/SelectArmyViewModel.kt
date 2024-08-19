package com.example.gravitygame.viewModels

import androidx.lifecycle.ViewModel
import com.example.gravitygame.uiStates.SelectArmyUiState
import com.example.gravitygame.models.ShipType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SelectArmyViewModel : ViewModel() {
    private val _selectArmyUiState = MutableStateFlow(SelectArmyUiState())
    val selectArmyUiState: StateFlow<SelectArmyUiState> = _selectArmyUiState.asStateFlow()

    fun addShip(ship: ShipType){
        when(ship){
            ShipType.CRUISER -> {
                val numberShips = selectArmyUiState.value.numberCruisers.inc()
                _selectArmyUiState.value = _selectArmyUiState.value.copy(numberCruisers = numberShips)
            }
            ShipType.DESTROYER -> {
                val numberShips = selectArmyUiState.value.numberDestroyers.inc()
                _selectArmyUiState.value = _selectArmyUiState.value.copy(numberDestroyers = numberShips)
            }
            ShipType.GHOST -> {
                val numberShips = selectArmyUiState.value.numberGhosts.inc()
                _selectArmyUiState.value = _selectArmyUiState.value.copy(numberGhosts = numberShips)
            }
            else -> {}
        }
    }

    fun removeShip(ship: ShipType){
        when(ship){
            ShipType.CRUISER -> {
                val numberShips = selectArmyUiState.value.numberCruisers.dec()
                _selectArmyUiState.value = _selectArmyUiState.value.copy(numberCruisers = numberShips)
            }
            ShipType.DESTROYER -> {
                val numberShips = selectArmyUiState.value.numberDestroyers.dec()
                _selectArmyUiState.value = _selectArmyUiState.value.copy(numberDestroyers = numberShips)
            }
            ShipType.GHOST -> {
                val numberShips = selectArmyUiState.value.numberGhosts.dec()
                _selectArmyUiState.value = _selectArmyUiState.value.copy(numberGhosts = numberShips)
            }
            else -> {}
        }
    }

    fun showShipInfoDialog(toShow: Boolean){
        if (toShow) {
            _selectArmyUiState.value = _selectArmyUiState.value.copy(showShipInfoDialog = true)
        } else {
            _selectArmyUiState.value = _selectArmyUiState.value.copy(showShipInfoDialog = false)
        }
    }

    fun cleanUiStates(){
        _selectArmyUiState.value = _selectArmyUiState.value.copy(
            numberCruisers = 0,
            numberDestroyers = 0,
            numberGhosts = 0
        )
    }
}