package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import javax.inject.Inject
import kotlin.math.min

/**
 * Use case responsible for accumulating expeditions from a given planet.
 * It calculates the number of expeditions points that can be generated and the
 * remaining rocket materials.
 */
class ProduceExpeditionsUseCase @Inject constructor() {
    /**
     * Calculates the number of possible expeditions and the remaining rocket materials for a given planet.
     *
     * This function determines how many expeditions can be launched from a planet based on its available
     * rocket materials and the maximum expeditions setting. It then returns a pair containing:
     *  - The number of possible expeditions that can be launched.
     *  - The number of rocket materials remaining after launching the expeditions.
     *
     * @param planet The Planet object containing information about rocket materials and expedition settings.
     * @return A Pair where:
     *          - The first element (Int) represents the number of possible expeditions.
     *          - The second element (Int) represents the number of remaining rocket materials.
     * @throws IllegalArgumentException if the planet's rocketMaterials or expeditionsSetting is negative
     */
    operator fun invoke(planet: Planet): Pair<Int, Int> {
        val district = planet.districts
            .filterIsInstance<District.ExpeditionPlatform>()
            .firstOrNull() ?: return Pair (0, planet.rocketMaterials)
        val productionRate = district.generateResources().produced[Resource.EXPEDITIONS] ?: 1
        val consumptionRate = district.generateResources().consumed[Resource.EXPEDITIONS] ?: 1
        val maxPossibleProduction = planet.rocketMaterials / consumptionRate
        val availableProduction = min(maxPossibleProduction * productionRate, planet.expeditionsSetting)
        return Pair(availableProduction, planet.rocketMaterials - (maxPossibleProduction * consumptionRate))
    }
}