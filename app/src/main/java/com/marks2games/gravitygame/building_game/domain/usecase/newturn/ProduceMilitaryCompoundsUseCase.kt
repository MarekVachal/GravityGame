package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import com.marks2games.gravitygame.building_game.domain.usecase.technology.ApplyDiversityTechnologyUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.technology.ApplySynergyUseCase
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.min

/**
 * Use case responsible for producing army units on a given planet.
 *
 * This class calculates the number of army units that can be produced based on the planet's
 * available resources (rocket materials), the production and consumption rates of the Expedition
 * Platform district, and the planet's army construction setting.
 *
 * @constructor Creates a ProduceArmyUnitUseCase instance. No dependencies are injected in this
 *              specific example, but the `@Inject constructor()` allows for potential dependency
 *              injection in a larger application.
 */
class ProduceMilitaryCompoundsUseCase @Inject constructor(
    private val applyDiversity: ApplyDiversityTechnologyUseCase,
    private val applySynergy: ApplySynergyUseCase
) {
    operator fun invoke(planet: Planet, technology: List<Technology>
    ): Pair<Int, Int> {
        val district = planet.districts
            .filterIsInstance<District.ExpeditionPlatform>()
            .firstOrNull() ?: return Pair(planet.army, planet.rocketMaterials)
        val productionRate = district.generateResources().produced[Resource.ARMY] ?: 0
        val consumptionRate = district.generateResources().consumed[Resource.ARMY] ?: 0
        val synergyBonus = applySynergy(
            resource = Resource.ARMY,
            districtId = district.districtId,
            planet = planet,
            technology = TechnologyEnum.EXPEDITION_SYNERGY,
            technologies = technology
        )
        val diversityBonus = applyDiversity.invoke(technology, DistrictEnum.EXPEDITION_PLATFORM, planet)
        val maxPossibleProduction = (planet.rocketMaterials.toDouble() / consumptionRate * productionRate).toInt()
        val availableProduction = min(maxPossibleProduction, planet.armyConstructionSetting)
        val finalProduction = (availableProduction + synergyBonus).toFloat() * diversityBonus
        val newCompounds = planet.army + floor(finalProduction).toInt()
        val newRocketMaterials = planet.rocketMaterials - availableProduction
        return Pair(newCompounds, newRocketMaterials)
    }
}