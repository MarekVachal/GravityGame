package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import android.util.Log
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.ProspectorsMode
import com.marks2games.gravitygame.building_game.data.model.Resource
import javax.inject.Inject
import kotlin.math.min

class GenerateMetalUseCase @Inject constructor(){
    private val TAG = "GenerateMetalUseCase"
    operator fun invoke(planet: Planet): Pair<Int, Int> {
        val prospectors = planet.districts.filterIsInstance<District.Prospectors>()

        Log.d(TAG, "Found ${prospectors.size} prospector districts")

        val activeMetalProspectors = prospectors.filter { it.isWorking && it.mode == ProspectorsMode.METAL }

        Log.d(TAG, "${activeMetalProspectors.size} are working and in METAL mode")
        val availableMetalProduction = activeMetalProspectors.sumOf {
            val generated = it.generateResources().produced[Resource.METAL] ?: 0
            Log.d(TAG, "District ${it.districtId} produces $generated metal")
            generated
        }
        val maxMetalProduction = min(availableMetalProduction, planet.planetMetal)
        val remainingPlanetMetal = planet.planetMetal - maxMetalProduction

        return Pair (planet.metal + maxMetalProduction, remainingPlanetMetal)
    }
}