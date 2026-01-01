package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import com.marks2games.gravitygame.building_game.domain.usecase.technology.ApplySynergyUseCase
import javax.inject.Inject
import kotlin.math.min

/**
 * Use case responsible for accumulating expeditions from a given planet.
 * It calculates the number of expeditions points that can be generated and the
 * remaining rocket materials.
 */
class ProduceExpeditionsUseCase @Inject constructor(
    private val applySynergy: ApplySynergyUseCase
) {
    operator fun invoke(planet: Planet, technologies: List<Technology>): Pair<Int, Int> {
        val district = planet.districts
            .filterIsInstance<District.ExpeditionPlatform>()
            .firstOrNull() ?: return Pair (0, planet.rocketMaterials)
        val productionRate = district.generateResources().produced[Resource.EXPEDITIONS] ?: 0
        val consumptionRate = district.generateResources().consumed[Resource.EXPEDITIONS] ?: 0
        val synergyBonus = applySynergy.invoke(
            resource = Resource.EXPEDITIONS,
            districtId = district.districtId,
            planet = planet,
            technology = TechnologyEnum.EXPEDITION_SYNERGY,
            technologies = technologies
        )
        val maxPossibleProduction = (planet.rocketMaterials.toDouble() / consumptionRate * productionRate).toInt()
        val availableProduction = min(maxPossibleProduction, planet.expeditionsSetting)
        val finalProduction = availableProduction + synergyBonus
        val newRocketMaterials = planet.rocketMaterials - availableProduction
        return Pair(finalProduction, newRocketMaterials)
    }
}