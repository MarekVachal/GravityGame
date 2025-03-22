package com.marks2games.gravitygame.battle_game.ui.screens.selectArmyScreen

import androidx.lifecycle.ViewModel
import com.marks2games.gravitygame.battle_game.data.SharedPlayerDataRepository
import com.marks2games.gravitygame.core.data.model.enum_class.ShipType
import com.marks2games.gravitygame.battle_game.ui.screens.battleMapScreen.BattleViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SelectArmyViewModel @Inject constructor(
    private val sharedPlayerData: SharedPlayerDataRepository
): ViewModel() {

    private val _selectArmyUiState = MutableStateFlow(SelectArmyUiState())
    val selectArmyUiState: StateFlow<SelectArmyUiState> = _selectArmyUiState.asStateFlow()

    fun isOnlineGame(): Boolean {
        return sharedPlayerData.getIsOnline()
    }

    fun checkArmySize(
        battleModel: BattleViewModel
    ): Boolean {
        val shipLimit = battleModel.battleMap.shipLimitOnMap
        return countArmySize() == shipLimit
    }

    fun countArmySize(): Int {
        return selectArmyUiState.let {
            it.value.numberCruisers +
            it.value.numberDestroyers +
            it.value.numberGhosts +
            1
        }
    }

    fun changeShipType(shipType: ShipType){
        _selectArmyUiState.update { state ->
            state.copy(shipType = shipType)
        }
    }

    fun addShip(ship: ShipType){
        when(ship){
            ShipType.CRUISER -> {
                val numberShips = selectArmyUiState.value.numberCruisers.inc()
                _selectArmyUiState.update { state ->
                    state.copy(numberCruisers = numberShips)
                }
            }
            ShipType.DESTROYER -> {
                val numberShips = selectArmyUiState.value.numberDestroyers.inc()
                _selectArmyUiState.update { state ->
                    state.copy(numberDestroyers = numberShips)
                }
            }
            ShipType.GHOST -> {
                val numberShips = selectArmyUiState.value.numberGhosts.inc()
                _selectArmyUiState.update { state ->
                    state.copy(numberGhosts = numberShips)
                }
            }
            else -> {}
        }
    }

    fun removeShip(ship: ShipType){
        when(ship){
            ShipType.CRUISER -> {
                val numberShips = selectArmyUiState.value.numberCruisers.dec()
                _selectArmyUiState.update { state ->
                    state.copy(numberCruisers = numberShips)
                }
            }
            ShipType.DESTROYER -> {
                val numberShips = selectArmyUiState.value.numberDestroyers.dec()
                _selectArmyUiState.update { state ->
                    state.copy(numberDestroyers = numberShips)
                }
            }
            ShipType.GHOST -> {
                val numberShips = selectArmyUiState.value.numberGhosts.dec()
                _selectArmyUiState.update { state ->
                    state.copy(numberGhosts = numberShips)
                }
            }
            else -> {}
        }
    }

    fun showShipInfoDialog(toShow: Boolean){
        _selectArmyUiState.update { state ->
            state.copy(showShipInfoDialog = toShow)
        }
    }

    fun cleanUiStates(){
        _selectArmyUiState.update { state ->
            state.copy(
                numberCruisers = 0,
                numberDestroyers = 0,
                numberGhosts = 0
            )
        }
    }
}