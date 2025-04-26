package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.ProspectorsMode
import com.marks2games.gravitygame.building_game.data.model.Resource
import javax.inject.Inject
import kotlin.math.min

class GenerateOrganicSedimentsUseCase @Inject constructor() {
    operator fun invoke(planet: Planet): Pair<Float, Float>{
        val availableOSProduction = planet.districts
            .filterIsInstance<District.Prospectors>()
            .filter { it.isWorking && it.mode == ProspectorsMode.ORGANIC_SEDIMENTS }
            .sumOf { it.generateResources().produced[Resource.ORGANIC_SEDIMENTS] ?: 1 }

        val maxOSProduction = min(availableOSProduction.toFloat(), planet.planetOrganicSediments)
        val remainingPlanetOS = planet.planetOrganicSediments - maxOSProduction

        return Pair (planet.organicSediment + maxOSProduction, remainingPlanetOS)
    }
}