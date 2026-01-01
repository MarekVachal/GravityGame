package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import com.marks2games.gravitygame.building_game.domain.usecase.technology.ApplyDiversityTechnologyUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.technology.ApplySynergyUseCase
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.min

class GenerateMetalByIndustrialsUseCase @Inject constructor(
    private val applyDiversity: ApplyDiversityTechnologyUseCase,
    private val applySynergy: ApplySynergyUseCase
) {
    operator fun invoke(planet: Planet, technologies: List<Technology>): Pair<Int, Float> {
        val exampleDistrict = planet.districts
            .filterIsInstance<District.Industrial>()
            .firstOrNull()?: return Pair(planet.metal, planet.organicSediment)
        val productionRate = exampleDistrict.generateResources().produced[Resource.METAL] ?: 0
        val consumptionRate = exampleDistrict.generateResources().consumed[Resource.ORGANIC_SEDIMENTS] ?: 0

        val industrials = planet.districts
            .filterIsInstance<District.Industrial>()
            .filter { it.isWorking && it.mode == IndustrialMode.METAL }

        val industrialMetalProduction: List<ProductionWithBonus> = industrials.map { district ->
            val baseProduction = district.generateResources().produced[Resource.METAL] ?: 0
            val synergyBonus = applySynergy.invoke(
                resource = Resource.METAL,
                districtId = district.districtId,
                planet = planet,
                technology = TechnologyEnum.INDUSTRIAL_SYNERGY,
                technologies = technologies
            )
            ProductionWithBonus(baseProduction, synergyBonus)
        }

        val totalBaseProduction = industrialMetalProduction.sumOf {it.base}
        val totalSynergyBonus = industrialMetalProduction.sumOf {it.bonus}
        val diversityBonus = applyDiversity.invoke(technologies, DistrictEnum.INDUSTRIAL, planet)

        val maxPossibleProduction = floor((planet.organicSediment / consumptionRate) * productionRate).toInt()
        val availableProduction = min(maxPossibleProduction, totalBaseProduction)
        val finalProduction = floor((availableProduction + totalSynergyBonus).toFloat() * diversityBonus).toInt()
        val consumedOS = (availableProduction.toFloat() / productionRate) * consumptionRate
        val newOS = planet.organicSediment - consumedOS
        return Pair (planet.metal + finalProduction, newOS)
    }
}