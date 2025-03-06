package com.marks2games.gravitygame.building_game.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.domain.usecase.GetEmpireUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.SaveOneTurnToBankUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.NewTurnResourcesProductionUseCase
import com.marks2games.gravitygame.core.domain.TimeProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmpireViewModel @Inject constructor(
    private val getEmpireUseCase: GetEmpireUseCase,
    private val newTurnResourcesProductionUseCase: NewTurnResourcesProductionUseCase,
    private val timeProvider: TimeProvider,
    private val saveOneTurnToBankUseCase: SaveOneTurnToBankUseCase
): ViewModel () {

    private val _empire = MutableStateFlow(Empire())
    val empire: StateFlow<Empire> = _empire.asStateFlow()

    fun getEmpireFromDatabase() {
        viewModelScope.launch {
            _empire.value = getEmpireUseCase.invoke()
        }
    }

    fun getEmpireState(): Empire {
        return empire.value
    }

    fun getPlanetsState(): List<Planet>{
        return empire.value.planets
    }

    fun makeTradepower(value: Int, planets: List<Planet>) {
        _empire.update { state ->
            state.copy(
                tradePower = value,
                planets = planets
            )
        }
    }

    fun createArmyUnit (value: Int, planets: List<Planet>){
        _empire.update { state ->
            state.copy(
                army = value,
                planets = planets
            )
        }
    }

    fun updateEmpire(empire: Empire){
        viewModelScope.launch {
            _empire.value = empire
        }
    }

    fun updatePlanets(planets: List<Planet>){
        _empire.update { state ->
            state.copy(
                planets = planets
            )
        }
    }

    fun increaseExpedition(value: Float, planets: List<Planet>) {
        _empire.update { state ->
            state.copy(
                expeditions = value,
                planets = planets
            )
        }
    }

    fun saveTurn(){
        viewModelScope.launch {
            val result = saveOneTurnToBankUseCase.invoke(empire.value)
            _empire.update { state ->
                state.copy(
                    savedTurns = result.first,
                    lastUpdated = result.second
                )
            }
        }
    }

    fun callNewTurn(){
        viewModelScope.launch {
            _empire.value = newTurnResourcesProductionUseCase.invoke(empire.value)
        }
    }

    fun getTimeToNextUpdate(): Int {
        return timeProvider.getSecondsToNextUpdate(empire.value.lastUpdated)
    }

    fun getTimeUnitMillis(): Long {
        return timeProvider.getTimeUnitMillis()
    }

}
