package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class SetResourcesProduceUseCase @Inject constructor() {
    operator fun invoke(planet: Planet, planetActions: List<Action>): Planet {
        return planetActions.fold(planet) { updatedPlanet, action ->
            when (action) {
                is Action.SetProduction.ExpeditionProduction -> updatedPlanet.copy(expeditionsSetting = action.setting)
                is Action.SetProduction.ProgressProduction -> updatedPlanet.copy(progressSetting = action.setting)
                is Action.SetProduction.ArmyProduction -> updatedPlanet.copy(armyConstructionSetting = action.setting)
                is Action.SetProduction.ResearchProduction -> updatedPlanet.copy(researchSetting = action.setting)
                is Action.SetProduction.InfrastructureProduction -> updatedPlanet.copy(infrastructureSetting = action.setting)
                is Action.SetProduction.RocketMaterialsProduction -> updatedPlanet.copy(rocketMaterialsSetting = action.setting)
                is Action.SetProduction.ShipTypeBuild -> updatedPlanet.copy(buildingShip = action.setting)
                else -> updatedPlanet
            }
        }
    }
}