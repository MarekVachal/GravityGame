package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.ProspectorsMode
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import com.marks2games.gravitygame.building_game.domain.usecase.technology.ApplyDiversityTechnologyUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.technology.ApplySynergyUseCase
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.min

class GenerateMetalByProspectorsUseCase @Inject constructor(
    private val applyDiversity: ApplyDiversityTechnologyUseCase,
    private val applySynergy: ApplySynergyUseCase
){
    operator fun invoke(planet: Planet, technologies: List<Technology>): Pair<Int, Int> {
        val prospectors = planet.districts.filterIsInstance<District.Prospectors>()
        val activeMetalProspectors = prospectors.filter { it.isWorking && it.mode == ProspectorsMode.METAL }
        val availableMetalProduction = activeMetalProspectors.sumOf {
            (it.generateResources().produced[Resource.METAL] ?: 0) +
                    applySynergy.invoke(
                        resource = Resource.METAL,
                        districtId = it.districtId,
                        planet = planet,
                        technology = TechnologyEnum.PROSPECTOR_SYNERGY,
                        technologies = technologies
                    )
        }
        val basicMetalProduction = min(availableMetalProduction, planet.planetMetal)
        val maxMetalProduction = basicMetalProduction.toFloat() * applyDiversity.invoke(technologies,
            DistrictEnum.PROSPECTORS, planet)
        val intProduction = floor(maxMetalProduction).toInt()
        val remainingPlanetMetal = planet.planetMetal - intProduction

        return Pair (planet.metal + intProduction, remainingPlanetMetal)
    }
}