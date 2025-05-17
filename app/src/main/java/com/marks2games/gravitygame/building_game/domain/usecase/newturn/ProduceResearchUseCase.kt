package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import android.util.Log
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
        val productionRate = exampleDistrict.generateResources().produced[Resource.RESEARCH] ?: 0
        val consumptionRate = exampleDistrict.generateResources().consumed[Resource.BIOMASS] ?: 0

        val totalResearchProgress = planet.districts
            .filterIsInstance<District.UrbanCenter>()
            .filter { it.mode == UrbanCenterMode.RESEARCH && it.isWorking }
            .sumOf { it.generateResources().produced[Resource.RESEARCH] ?: 0 }
        Log.d("Research", "Total research progress: $totalResearchProgress")

        val maxPossibleProduction = floor(planet.biomass / consumptionRate).toInt()
        Log.d("Research", "Max possible production: $maxPossibleProduction")
        val availableProduction = min(maxPossibleProduction * productionRate, planet.researchSetting)
        Log.d("Research", "Available production: $availableProduction")
        val minimalProduction = min(totalResearchProgress, availableProduction)
        Log.d("Research", "Minimal production: $minimalProduction")
        val consumedBiomass = (minimalProduction.toFloat() / productionRate) * consumptionRate
        Log.d("Research", "Consumed biomass: $consumedBiomass")
        val newBiomass = planet.biomass - consumedBiomass
        Log.d("Research", "New biomass: $newBiomass")

        return Pair(
            minimalProduction,
            newBiomass
        )
    }
}