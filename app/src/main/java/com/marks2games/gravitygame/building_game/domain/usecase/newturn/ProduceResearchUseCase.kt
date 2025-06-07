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
import kotlin.math.min

class ProduceResearchUseCase @Inject constructor(
    private val applyDiversity: ApplyDiversityTechnologyUseCase,
    private val applySynergy: ApplySynergyUseCase
){
    operator fun invoke(planet: Planet, technologies: List<Technology>): Pair <Int, Float> {
        val exampleDistrict = planet.districts
            .filterIsInstance<District.UrbanCenter>()
            .firstOrNull() ?: return Pair(0, planet.biomass)
        val productionRate = exampleDistrict.generateResources().produced[Resource.RESEARCH] ?: 0
        val consumptionRate = exampleDistrict.generateResources().consumed[Resource.BIOMASS] ?: 0

        val urbanDistricts = planet.districts
            .filterIsInstance<District.UrbanCenter>()
            .filter { it.mode == UrbanCenterMode.RESEARCH && it.isWorking }

        val urbanProduction: List<ProductionWithBonus> = urbanDistricts.map{ district ->
            val baseProduction = district.generateResources().produced[Resource.RESEARCH] ?: 0
            val synergyBonus = applySynergy.invoke(
                resource = Resource.RESEARCH,
                districtId = district.districtId,
                planet = planet,
                technology = TechnologyEnum.URBAN_CENTER_SYNERGY,
                technologies = technologies
            )
            ProductionWithBonus(baseProduction, synergyBonus)
        }

        val totalBaseProduction = urbanProduction.sumOf { it.base }
        val totalSynergyBonus = urbanProduction.sumOf { it.bonus }
        val diversityBonus = applyDiversity.invoke(technologies, DistrictEnum.URBAN_CENTER, planet)

        val maxPossibleProduction = floor((planet.biomass / consumptionRate) * productionRate).toInt()
        val availableProduction = min(maxPossibleProduction, planet.researchSetting)
        val minimalProduction = min(totalBaseProduction, availableProduction)
        val finalProduction = floor((minimalProduction + totalSynergyBonus).toFloat() * diversityBonus).toInt()
        val consumedBiomass = (minimalProduction.toFloat() / productionRate) * consumptionRate
        val newBiomass = planet.biomass - consumedBiomass

        return Pair(
            finalProduction,
            newBiomass
        )
    }
}