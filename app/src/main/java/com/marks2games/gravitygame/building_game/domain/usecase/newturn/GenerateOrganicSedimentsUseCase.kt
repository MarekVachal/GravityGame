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
import kotlin.math.min

class GenerateOrganicSedimentsUseCase @Inject constructor(
    private val applyDiversity: ApplyDiversityTechnologyUseCase,
    private val applySynergy: ApplySynergyUseCase
) {
    operator fun invoke(planet: Planet, technologies: List<Technology>): Pair<Float, Float>{
        val availableOSProduction = planet.districts
            .filterIsInstance<District.Prospectors>()
            .filter { it.isWorking && it.mode == ProspectorsMode.ORGANIC_SEDIMENTS }
            .sumOf { (it.generateResources().produced[Resource.ORGANIC_SEDIMENTS] ?: 0) +
                applySynergy.invoke(
                    resource = Resource.ORGANIC_SEDIMENTS,
                    districtId = it.districtId,
                    planet = planet,
                    technology = TechnologyEnum.PROSPECTOR_SYNERGY,
                    technologies = technologies
                )
            }

        val basicOSProduction = min(availableOSProduction.toFloat(), planet.planetOrganicSediments)
        val maxOSProduction = basicOSProduction.toFloat() * applyDiversity.invoke(technologies,
            DistrictEnum.EMPTY, planet)
        val remainingPlanetOS = planet.planetOrganicSediments - maxOSProduction

        return Pair (planet.organicSediment + maxOSProduction, remainingPlanetOS)
    }
}