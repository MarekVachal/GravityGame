package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import com.marks2games.gravitygame.building_game.data.model.UrbanCenterMode
import com.marks2games.gravitygame.building_game.domain.usecase.technology.ApplyDiversityTechnologyUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.technology.ApplySynergyUseCase
import javax.inject.Inject
import kotlin.math.floor

class GenerateInfluenceUseCase @Inject constructor(
    private val applyDiversity: ApplyDiversityTechnologyUseCase,
    private val applySynergy: ApplySynergyUseCase
){
    operator fun invoke(planet: Planet, technologies: List<Technology>): Int {
        val basicProduction = planet.districts
            .filterIsInstance<District.UrbanCenter>()
            .filter { it.isWorking && it.mode == UrbanCenterMode.INFLUENCE }
            .sumOf { (it.generateResources().produced[Resource.INFLUENCE] ?: 0) + applySynergy.invoke(
                resource = Resource.INFLUENCE,
                districtId = it.districtId,
                planet = planet,
                technology = TechnologyEnum.URBAN_CENTER_SYNERGY,
                technologies = technologies
            ) }
        val updatedProductionByDiversity = basicProduction.toFloat() * applyDiversity.invoke(technologies,
            DistrictEnum.URBAN_CENTER, planet)
        return floor(updatedProductionByDiversity).toInt()
    }
}