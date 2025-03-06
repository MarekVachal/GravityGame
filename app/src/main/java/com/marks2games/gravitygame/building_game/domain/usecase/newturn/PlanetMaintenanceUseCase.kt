package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject
import kotlin.math.pow

class PlanetMaintenanceUseCase @Inject constructor(){
    operator fun invoke(planet: Planet): Planet {
        var maintenance = ((planet.level - 9).toDouble()).pow(2) / 10
        var planetDegraded = false
        var infrastructure = planet.infrastructure
        var biomass = planet.biomass
        var influence = planet.influence
        var planetLevel = planet.level
        while (maintenance > 0 || !planetDegraded) {
            if (
                infrastructure != 0 &&
                biomass >= 1f &&
                influence != 0
            ) {
                maintenance -= 1
                infrastructure -= 1
                biomass -= 1f
                influence -= 1
            } else {
                planetLevel -= 1
                planetDegraded = true
            }
        }
        return planet.copy(
            level = planetLevel,
            biomass = biomass,
            infrastructure = infrastructure,
            influence = influence
        )
    }
}