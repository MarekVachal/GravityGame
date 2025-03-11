package com.marks2games.gravitygame.building_game.domain.usecase.planetaction

import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class CreateArmyUnitUseCase @Inject constructor() {
    operator fun invoke(
        value: Int,
        planet: Planet,
        empire: Empire,
        createArmyUnit: (Int, List<Planet>) -> Unit
    ): Planet {
        var rocketMaterial = planet.rocketMaterials
        var army = empire.army
        var updatedPlanet = planet

        if(rocketMaterial >= value) {
            rocketMaterial -= value
            army += value
            updatedPlanet = planet.copy(rocketMaterials = rocketMaterial)
            val planets = empire.planets.map {
                if (it.id == planet.id) {
                    updatedPlanet
                } else {
                    it
                }
            }
            createArmyUnit(army, planets)
        }
        return updatedPlanet
    }
}