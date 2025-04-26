package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class SetResourcesProduceUseCase @Inject constructor() {
    operator fun invoke(planet: Planet, planetActions: List<Action>): Planet {
        return planetActions.fold(planet) { updatedPlanet, action ->
            when (action) {
                is Action.SetProduction.ExpeditionProduction -> updatedPlanet.copy(expeditionsSetting = action.value)
                is Action.SetProduction.ProgressProduction -> updatedPlanet.copy(progressSetting = action.value)
                is Action.SetProduction.ArmyProduction -> updatedPlanet.copy(armyConstructionSetting = action.value)
                is Action.SetProduction.ResearchProduction -> updatedPlanet.copy(researchSetting = action.value)
                is Action.SetProduction.InfrastructureProduction -> updatedPlanet.copy(infrastructureSetting = action.value)
                is Action.SetProduction.RocketMaterialsProduction -> updatedPlanet.copy(rocketMaterialsSetting = action.value)
                else -> updatedPlanet
            }
        }
    }
}