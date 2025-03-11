package com.marks2games.gravitygame.building_game.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Trade
import com.marks2games.gravitygame.building_game.domain.usecase.planetaction.TradeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TradeViewModel @Inject constructor(
    private val tradeUseCase: TradeUseCase
) : ViewModel(){

    private val _tradeUiState = MutableStateFlow(Trade())
    val tradeUiState: StateFlow<Trade> = _tradeUiState

    fun trade(
        empire: Empire,
        updatedEmpire: (Empire) -> Unit
    ){
        viewModelScope.launch {
            updatedEmpire(tradeUseCase.invoke(empire, tradeUiState.value))
        }
    }
}