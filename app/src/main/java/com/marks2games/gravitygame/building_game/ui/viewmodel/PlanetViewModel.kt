package com.marks2games.gravitygame.building_game.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.ProspectorsMode
import com.marks2games.gravitygame.building_game.data.model.UrbanCenterMode
import com.marks2games.gravitygame.building_game.domain.usecase.AccumulateDevelopmentUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.AccumulateExpeditionsUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.AccumulateProgressUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.BuildDistrictUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.ChangeDistrictModeUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.CreateArmyUnitUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.DestroyDistrictUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.GetPlanetFromDatabaseUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.GetPlanetUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.MakeTradepowerUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.UpdatePlanetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanetViewModel @Inject constructor(
    private val getPlanetFromDatabaseUseCase: GetPlanetFromDatabaseUseCase,
    private val updatePlanetUseCase: UpdatePlanetUseCase,
    private val buildDistrictUseCase: BuildDistrictUseCase,
    private val destroyDistrictUseCase: DestroyDistrictUseCase,
    private val changeDistrictModeUseCase: ChangeDistrictModeUseCase,
    private val accumulateProgressUseCase: AccumulateProgressUseCase,
    private val getPlanetUseCase: GetPlanetUseCase,
    private val makeTradepowerUseCase: MakeTradepowerUseCase,
    private val accumulateDevelopmentUseCase: AccumulateDevelopmentUseCase,
    private val accumulateExpeditionsUseCase: AccumulateExpeditionsUseCase,
    private val createArmyUnitUseCase: CreateArmyUnitUseCase
) : ViewModel() {

    private val _planet = MutableStateFlow(Planet())
    val planet: StateFlow<Planet> = _planet

    fun loadPlanet(planets: List<Planet>, planetId: Int?){
        if(planetId == null){
         // Show error Dialog and navigate back
        } else {
            val planet = getPlanetUseCase.invoke(planets, planetId)
            if(planet == null){
                // Show error Dialog and navigate back
            } else {
                _planet.value = planet
            }
        }
    }

    fun loadPlanetFromDatabase(planetId: Int) {
        viewModelScope.launch {
            _planet.value = getPlanetFromDatabaseUseCase.invoke(planetId)
        }
    }

    fun makeTradepower(value: Int, empire: Empire, increaseTradepower: (Int, List<Planet>) -> Unit) {
        viewModelScope.launch {
            _planet.value = makeTradepowerUseCase.invoke(
                value = value,
                planet = planet.value,
                empire = empire,
                increaseTradepower = increaseTradepower
            )
        }
    }

    fun accumulateDevelopment(value: Int){
        viewModelScope.launch {
            _planet.value = accumulateDevelopmentUseCase.invoke(
                value = value,
                planet = planet.value
            )
        }
    }

    fun createArmyUnit(
        value: Int,
        empire: Empire,
        createArmyUnit: (Int, List<Planet>) -> Unit
    ){
        viewModelScope.launch {
            _planet.value = createArmyUnitUseCase.invoke(
                value = value,
                planet = planet.value,
                empire = empire,
                createArmyUnit = createArmyUnit
            )
        }
    }

    fun accumulateExpeditions(
        value: Int,
        empire: Empire,
        increaseExpeditions: (Float, List<Planet>) -> Unit
    ){
        viewModelScope.launch {
            _planet.value = accumulateExpeditionsUseCase.invoke(
                value = value,
                planet = planet.value,
                empire = empire,
                increaseExpeditions = increaseExpeditions
            )
        }
    }

    fun changeProspectorsMode(mode: ProspectorsMode){
        viewModelScope.launch {
            _planet.value = changeDistrictModeUseCase.invoke(planet.value, DistrictEnum.PROSPECTORS, mode)
        }
    }

    fun changeIndustrialMode(mode: IndustrialMode){
        viewModelScope.launch {
            _planet.value = changeDistrictModeUseCase.invoke(planet.value, DistrictEnum.INDUSTRIAL, mode)
        }
    }

    fun changeUrbanCenterMode(mode: UrbanCenterMode){
        viewModelScope.launch {
            _planet.value = changeDistrictModeUseCase.invoke(planet.value, DistrictEnum.URBAN_CENTER, mode)
        }
    }

    fun destroyDistrict(districtToDestroy: DistrictEnum){
        viewModelScope.launch {
            _planet.value = destroyDistrictUseCase.invoke(planet.value, districtToDestroy)
        }
    }

    fun buildNewDistrict(districtToBuild: DistrictEnum){
        viewModelScope.launch {
            _planet.value = buildDistrictUseCase.invoke(planet.value, districtToBuild)
        }
    }

    fun accumulateProgress(value: Int){
        viewModelScope.launch {
            _planet.value = accumulateProgressUseCase.invoke(value, planet.value)
        }
    }

    fun updatePlanet() {
        viewModelScope.launch {
            updatePlanetUseCase.invoke(planet.value)
        }
    }
}