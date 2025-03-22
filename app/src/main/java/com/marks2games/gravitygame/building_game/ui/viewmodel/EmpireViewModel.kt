package com.marks2games.gravitygame.building_game.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marks2games.gravitygame.building_game.data.model.ActionEnum
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.EmpireUiState
import com.marks2games.gravitygame.building_game.data.model.InfrastructureSetting
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.RocketMaterialsSetting
import com.marks2games.gravitygame.building_game.data.model.Trade
import com.marks2games.gravitygame.building_game.data.model.Transport
import com.marks2games.gravitygame.building_game.domain.usecase.GetEmpireUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.NewTurnUseClass
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddArmyProductionActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddExpeditionProductionActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddInfrastructureProductionActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddProgressProductionActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddResearchProductionActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddRocketMaterialsProductionActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddTradeActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.AddTransportActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.BuildDistrictActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.CancelTransportUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.ChangeDistrictModeActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.DestroyDistrictActionUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.useractions.SaveEmpireUseCase
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
    private val newTurnUseCase: NewTurnUseClass,
    private val saveEmpireUseCase: SaveEmpireUseCase,
    private val addArmyProductionActionUseCase: AddArmyProductionActionUseCase,
    private val addExpeditionProductionActionUseCase: AddExpeditionProductionActionUseCase,
    private val addInfrastructureProductionActionUseCase: AddInfrastructureProductionActionUseCase,
    private val addProgressProductionActionUseCase: AddProgressProductionActionUseCase,
    private val addResearchProductionActionUseCase: AddResearchProductionActionUseCase,
    private val addRocketMaterialsProductionActionUseCase: AddRocketMaterialsProductionActionUseCase,
    private val addTradeActionUseCase: AddTradeActionUseCase,
    private val addTransportActionUseCase: AddTransportActionUseCase,
    private val buildDistrictActionUseCase: BuildDistrictActionUseCase,
    private val cancelTransportUseCase: CancelTransportUseCase,
    private val changeDistrictModeActionUseCase: ChangeDistrictModeActionUseCase,
    private val destroyDistrictActionUseCase: DestroyDistrictActionUseCase
): ViewModel () {

    private val _empire = MutableStateFlow(Empire())
    val empire: StateFlow<Empire> = _empire.asStateFlow()
    private val _empireUiState = MutableStateFlow(EmpireUiState())
    val empireUiState: StateFlow<EmpireUiState> = _empireUiState.asStateFlow()


    fun getEmpireFromDatabase() {
        viewModelScope.launch {
            _empire.value = getEmpireUseCase.invoke().copy()
        }
    }

    fun getEmpireState(): Empire {
        return empire.value
    }

    fun getPlanetsState(): List<Planet> {
        return empire.value.planets
    }

    fun updateEmpire(empire: Empire) {
        viewModelScope.launch {
            _empire.value = empire
        }
    }

    fun updatePlanets(planets: List<Planet>) {
        _empire.update { state ->
            state.copy(
                planets = planets
            )
        }
    }

    fun testNewTurn() {
        val newTurnTestResult = newTurnUseCase.invoke(empire.value, false)
        val updatedEmpire = newTurnTestResult.first
        _empire.update { state -> updatedEmpire }
        _empireUiState.update { state ->
            state.copy(
                errors = newTurnTestResult.second
            )
        }
    }

    fun updateErrorsShown(isShown: Boolean) {
        _empireUiState.update { state ->
            state.copy(
                isErrorsShown = isShown
            )
        }
    }

    fun updateActionsShown(isShown: Boolean) {
        _empireUiState.update { state ->
            state.copy(
                isActionsShown = isShown
            )
        }
    }

    fun newTurn(){
        viewModelScope.launch {
            val newTurnResult = newTurnUseCase.invoke(empire.value, false)
            val updatedEmpire = newTurnResult.first
            Log.d("NewTurn", "New empire state: ${newTurnResult.first}")
            _empire.update { updatedEmpire }
            _empireUiState.update { state ->
                state.copy(
                    errors = emptyList()
                )
            }
            saveEmpireUseCase.invoke(updatedEmpire)
            Log.d("ViewModel", "Empire saved")
        }
    }

    fun updateIntStates(value: Int, action: ActionEnum, planetId: Int){
        _empire.update { state ->
            state.copy(
                planets = state.planets.map{ planet ->
                    when (action){
                        ActionEnum.EXPEDITIONS_ACTION -> planet.copy(expeditionsSetting = value)
                        ActionEnum.PROGRESS_ACTION -> planet.copy(progressSetting = value)
                        ActionEnum.ARMY_ACTION -> planet.copy(armyConstructionSetting = value)
                        ActionEnum.RESEARCH_ACTION -> planet.copy(researchSetting = value)
                        else -> throw Exception("Unsupported action.")
                    }
                }
            )

        }
    }

    fun updateInfrastructureStates(value: InfrastructureSetting, planetId: Int){
        _empire.update { state ->
            state.copy(
                planets = state.planets.map { planet ->
                    if (planet.id == planetId) planet.copy(infrastructureSetting = value)
                    else planet
                }
            )
        }
    }

    fun updateRocketMaterialsStates(value: RocketMaterialsSetting, planetId: Int){
        _empire.update { state ->
            state.copy(
                planets = state.planets.map { planet ->
                    if (planet.id == planetId) planet.copy(rocketMaterialsSetting = value)
                    else planet
                }
            )
        }
    }

    fun addArmyProductionAction(planetId: Int){
        _empire.update { state ->
            state.copy(
                planets = state.planets.map { planet ->
                    if (planet.id == planetId) planet.copy(
                        actions = addArmyProductionActionUseCase.invoke(
                            planet.actions,
                            planet.id,
                            state.planets[planetId].armyConstructionSetting
                        )
                    ) else planet
                }
            )
        }
    }

    fun addExpeditionProductionAction(planetId: Int){
        _empire.update { state ->
            state.copy(
                planets = state.planets.map { planet ->
                    if(planet.id == planetId) planet.copy(
                        actions = addExpeditionProductionActionUseCase.invoke(
                            planet.actions,
                            planet.id,
                            state.planets[planetId].expeditionsSetting
                        )
                    ) else planet
                }
            )
        }
    }

    fun addInfrastructureProductionAction(planetId: Int){
        _empire.update { state ->
            state.copy(
                planets = state.planets.map { planet ->
                    if(planet.id == planetId) planet.copy(
                        actions = addInfrastructureProductionActionUseCase.invoke(
                            planet.actions,
                            planet.id,
                            state.planets[planetId].infrastructureSetting
                        )
                    ) else planet
                }
            )
        }
    }

    fun addProgressProductionAction(planetId: Int){
        _empire.update { state ->
            state.copy(
                planets = state.planets.map { planet ->
                    if(planet.id == planetId) planet.copy(
                        actions = addProgressProductionActionUseCase.invoke(
                            planet.actions,
                            planet.id,
                            state.planets[planetId].progressSetting
                        )
                    ) else planet
                }
            )
        }
    }

    fun addResearchProductionAction(planetId: Int){
        _empire.update { state ->
            state.copy(
                planets = state.planets.map { planet ->
                    if(planet.id == planetId) planet.copy(
                        actions = addResearchProductionActionUseCase.invoke(
                            planet.actions,
                            planet.id,
                            state.planets[planetId].researchSetting
                        )
                    ) else planet
                }
            )
        }
    }

    fun addRocketMaterialsProductionAction(planetId: Int){
        _empire.update { state ->
            state.copy(
                planets = state.planets.map { planet ->
                    if(planet.id == planetId) planet.copy(
                        actions = addRocketMaterialsProductionActionUseCase.invoke(
                            planet.actions,
                            planet.id,
                            state.planets[planetId].rocketMaterialsSetting
                        )
                    ) else planet
                }
            )
        }
    }

    fun addTradeAction(planetId: Int, trade: Trade){
        _empire.update { state ->
            state.copy(
                planets = state.planets.map { planet ->
                    if(planet.id == planetId) planet.copy(
                        actions = addTradeActionUseCase.invoke(
                            planet.actions,
                            planet.id,
                            trade
                        )
                    ) else planet
                }
            )
        }
    }

    fun addTransportAction(planetId: Int, transport: Transport){
        _empire.update { state ->
            state.copy(
                planets = state.planets.map { planet ->
                    if(planet.id == planetId) planet.copy(
                        actions = addTransportActionUseCase.invoke(
                            planet.actions,
                            planet.id,
                            transport
                        )
                    ) else planet
                }
            )
        }
    }

    fun buildDistrictAction(planetId: Int, district: DistrictEnum, districtId: Int){
        _empire.update { state ->
            state.copy(
                planets = state.planets.map { planet ->
                    if(planet.id == planetId) planet.copy(
                        actions = buildDistrictActionUseCase.invoke(
                            actions = planet.actions,
                            planetId = planet.id,
                            districtId = districtId,
                            district = district
                        )
                    ) else planet
                }
            )
        }
    }

    fun cancelTransportAction(){
        cancelTransportUseCase.invoke()
    }

    fun changeDistrictModeAction(
        district: DistrictEnum,
        districtId: Int,
        mode: Enum<*>,
        planetId: Int
    ) {
        _empire.update { state ->
            state.copy(
                planets = state.planets.map { planet ->
                    if(planet.id == planetId) planet.copy(
                        actions = changeDistrictModeActionUseCase.invoke(
                            planet.actions,
                            planet.id,
                            districtId,
                            district,
                            mode
                        )
                    ) else planet
                }
            )
        }
    }

    fun destroyDistrictAction(planetId: Int,districtId: Int){
        _empire.update { state ->
            state.copy(
                planets = state.planets.map { planet ->
                    if(planet.id == planetId) planet.copy(
                        actions = destroyDistrictActionUseCase.invoke(
                            planet.actions,
                            planet.id,
                            districtId
                        )
                    ) else planet
                }
            )
        }
    }

}
