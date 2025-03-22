package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.min

/**
 * Use case for calculating progress points made on a planet based on its resources and settings.
 *
 * This class is responsible for determining how much progress points can be produced in a single turn
 * based on the available infrastructure, biomass, and the planet's progress setting.
 *
 * @constructor Creates a [ProduceProgressUseCase] instance. No external dependencies are needed.
 */
class ProduceProgressUseCase @Inject constructor(
) {
    /**
     * Simulates a single turn of resource processing on a given planet.
     *
     * This function calculates the progress points production, decrease infrastructure and biomass based on progress points production.
     *
     * @param planet The [Planet] object representing the planet's current state (infrastructure, biomass, progress, progress setting).
     * @return A [Triple] containing three values:
     *   - first: The updated progress points of the planet after the turn. (Int)
     *   - second: The remaining infrastructure after resource consumption. (Int)
     *   - third: The remaining biomass after resource consumption. (Float)
     *
     * The calculation follows these steps:
     * 1. **Determine the minimal resource:** The minimum value between the planet's infrastructure and the floor (integer part) of its biomass is selected.
     * 2. **Determine the minimal production:** The minimum value between the minimal resource and the planet's progress setting is selected. This represents the actual amount of resources processed.
     * 3. **Calculate the new progress points:** The planet's current progress points are incremented by the minimal production.
     * 4. **Calculate the remaining infrastructure:** The planet's infrastructure is decremented by the minimal production.
     * 5. **Calculate the remaining biomass:** The planet's biomass is decremented by the minimal resource.
     * 6. return a Triple with the calculated values.
     */
    operator fun invoke(planet: Planet) : Triple<Int, Int, Float> {
        val exampleDistrict = planet.districts.filterIsInstance<District.Capitol>().first()
        val productionRate = exampleDistrict.generateResources().produced[Resource.PROGRESS] ?: 1
        val consumptionBiomassRate = exampleDistrict.generateResources().consumed[Resource.BIOMASS]?: 1
        val consumptionInfrastructureRate = exampleDistrict.generateResources().consumed[Resource.INFRASTRUCTURE]?: 1

        val maxBiomassBasedProduction = floor(planet.biomass / consumptionBiomassRate).toInt()
        val maxInfrastructureBasedProduction = planet.infrastructure / consumptionInfrastructureRate
        val availableResource = min(maxBiomassBasedProduction, maxInfrastructureBasedProduction)
        val availableProduction = min(availableResource * productionRate, planet.progressSetting)

        return Triple(
            planet.progress + availableProduction,
            planet.infrastructure - (availableProduction / productionRate) * consumptionInfrastructureRate,
            planet.biomass - (availableProduction / productionRate) * consumptionBiomassRate
        )
    }
}