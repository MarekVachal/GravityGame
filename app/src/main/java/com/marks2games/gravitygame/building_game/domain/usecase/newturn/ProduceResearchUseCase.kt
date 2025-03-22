package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.UrbanCenterMode
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.min

class ProduceResearchUseCase @Inject constructor(){
    operator fun invoke(planet: Planet): Pair <Int, Float> {
        val exampleDistrict = planet.districts
            .filterIsInstance<District.UrbanCenter>()
            .firstOrNull() ?: return Pair(0, planet.biomass)
        val productionRate = exampleDistrict.generateResources().produced[Resource.PROGRESS] ?: 1
        val consumptionRate = exampleDistrict.generateResources().consumed[Resource.BIOMASS] ?: 1

        val totalResearchProgress = planet.districts
            .filterIsInstance<District.UrbanCenter>()
            .filter { it.mode == UrbanCenterMode.RESEARCH && it.isWorking }
            .sumOf { it.generateResources().produced[Resource.PROGRESS] ?: 0 }

        val maxPossibleProduction = (floor(planet.biomass).toInt()) / consumptionRate
        val availableProduction = min(maxPossibleProduction * productionRate, planet.researchSetting)
        val minimalProduction = min(totalResearchProgress, availableProduction)

        return Pair(
            minimalProduction,
            planet.biomass - (minimalProduction / productionRate * consumptionRate).toFloat()
        )
    }
}