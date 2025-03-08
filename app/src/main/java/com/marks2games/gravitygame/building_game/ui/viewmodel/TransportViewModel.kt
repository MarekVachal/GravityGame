package com.marks2games.gravitygame.building_game.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Transport
import com.marks2games.gravitygame.building_game.domain.usecase.planetaction.TransportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransportViewModel @Inject constructor (
    private val transportUseCase: TransportUseCase
): ViewModel() {

    private val _transportUiState = MutableStateFlow(Transport())
    val transportUiState: StateFlow<Transport> = _transportUiState

    fun transport(planets: List<Planet>, updatePlanets: (List<Planet>) -> Unit){
        viewModelScope.launch {
            transportUseCase.invoke(
                transport = transportUiState.value,
                planets = planets,
                updatePlanets = updatePlanets
            )
        }
    }

}