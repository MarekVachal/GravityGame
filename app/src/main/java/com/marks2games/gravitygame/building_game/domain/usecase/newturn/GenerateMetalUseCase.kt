package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.ProspectorsMode
import com.marks2games.gravitygame.building_game.data.model.Resource
import javax.inject.Inject
import kotlin.math.min

class GenerateMetalUseCase @Inject constructor(){
    operator fun invoke(planet: Planet): Pair<Int, Int> {
         val availableMetalProduction = planet.districts
            .filterIsInstance<District.Prospectors>()
            .filter { it.isWorking && it.mode == ProspectorsMode.METAL }
            .sumOf { it.generateResources().produced[Resource.METAL] ?: 1 }

        val maxMetalProduction = min(availableMetalProduction, planet.planetMetal)
        val remainingPlanetMetal = planet.planetMetal - maxMetalProduction

        return Pair (maxMetalProduction, remainingPlanetMetal)
    }
}