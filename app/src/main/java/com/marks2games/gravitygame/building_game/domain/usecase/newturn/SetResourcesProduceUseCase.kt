package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.Action
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class SetResourcesProduceUseCase @Inject constructor() {
    operator fun invoke(planet: Planet): Planet {
        val updatedPlanet = planet
        planet.actions
            .filterIsInstance<Action.SetProduction>()
            .forEach {
                when (it) {
                    is Action.SetProduction.ExpeditionProduction -> updatedPlanet.copy(
                        expeditionsSetting = it.value)
                    is Action.SetProduction.ArmyProduction -> updatedPlanet.copy(
                        armyConstructionSetting = it.value)
                    is Action.SetProduction.InfrastructureProduction -> updatedPlanet.copy(
                        infrastructureSetting = it.value)
                    is Action.SetProduction.ProgressProduction -> updatedPlanet.copy(
                        progressSetting = it.value)
                    is Action.SetProduction.ResearchProduction -> updatedPlanet.copy(
                        researchSetting = it.value)
                    is Action.SetProduction.RocketMaterialsProduction -> updatedPlanet.copy(
                        rocketMaterialsSetting = it.value
                    )
                }
            }
        return planet
    }
}