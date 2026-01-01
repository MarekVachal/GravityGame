package com.marks2games.gravitygame.battle_game.data.room_database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marks2games.gravitygame.ui.screens.statisticScreen.StatisticUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DatabaseViewModel (
    private val repository: BattleRepository
): ViewModel(){

    private val _statisticsUiState = MutableStateFlow(StatisticUiState())
    val statisticUiState: StateFlow<StatisticUiState> = _statisticsUiState.asStateFlow()

    fun onItemClick(battleResult: BattleResult?){
        _statisticsUiState.value = _statisticsUiState.value.copy(battleResult = battleResult)
    }

    fun nullBattleResult(){
        _statisticsUiState.value = _statisticsUiState.value.copy(battleResult = null)
    }

    fun loadStatistic(){
        getTotalBattles()
        getAverageTurn()
        getTotalMyShipLost()
        getTotalEnemyShipDestroyed()
        getCountOfDraws()
        getCountOfLost()
        getCountOfWins()
        getAllResults()
    }

    fun insertBattleResult(battleResult: BattleResult){
        viewModelScope.launch{
            repository.insertBattleResult(battleResult = battleResult)
        }
    }

    private fun getTotalMyShipLost(){
        viewModelScope.launch {
            val total = repository.getTotalMyShipLost()
            _statisticsUiState.value = _statisticsUiState.value.copy(totalMyShipLost = total)
        }
    }

    private fun getAllResults(){
        viewModelScope.launch {
            val total = repository.getAllResults()
            _statisticsUiState.value = _statisticsUiState.value.copy(listBattleResult = total)
        }
    }

    private fun getTotalEnemyShipDestroyed(){
        viewModelScope.launch {
            val total = repository.getTotalEnemyShipDestroyed()
            _statisticsUiState.value = _statisticsUiState.value.copy(totalEnemyShipDestroyed = total)
        }
    }

    private fun getAverageTurn(){
        viewModelScope.launch {
            val total = repository.getAverageTurn().toInt()
            _statisticsUiState.value = _statisticsUiState.value.copy(averageTurn = total)
        }
    }

    private fun getTotalBattles(){
        viewModelScope.launch {
            val total = repository.getTotalBattles()
            _statisticsUiState.value = _statisticsUiState.value.copy(totalBattle = total)
        }
    }

    private fun getCountOfWins(){
        viewModelScope.launch {
            val total = repository.getCountOfWins()
            _statisticsUiState.value = _statisticsUiState.value.copy(countOfWins = total)
        }
    }

    private fun getCountOfLost(){
        viewModelScope.launch {
            val total = repository.getCountOfLost()
            _statisticsUiState.value = _statisticsUiState.value.copy(countOfLost = total)
        }
    }

    private fun getCountOfDraws(){
        viewModelScope.launch {
            val total = repository.getCountOfDraw()
            _statisticsUiState.value = _statisticsUiState.value.copy(countOfDraw = total)
        }
    }

}