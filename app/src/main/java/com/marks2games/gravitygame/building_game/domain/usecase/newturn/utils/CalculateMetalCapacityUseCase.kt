package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import javax.inject.Inject

/**
 * Use case responsible for calculating the metal capacity of a given planet.
 *
 * The metal capacity is determined by the number of PROSPECTORS districts on the planet.
 * Each PROSPECTORS district contributes 10 units to the total metal capacity.
 */
class CalculateMetalCapacityUseCase @Inject constructor() {
    /**
     * Calculates the score contribution of a given planet based on its prospector districts.
     *
     * This function determines the score a planet contributes by counting the number of
     * districts on the planet that are of type `DistrictEnum.PROSPECTORS` and then
     * multiplying that count by 10.
     *
     * @param planet The [Planet] object for which to calculate the score.
     * @return An [Int] representing the total score contribution of the planet's prospector districts.
     */
    operator fun invoke(planet: Planet): Int {
        return planet.districts
            .sumOf { it.getCapacities().capacity[Resource.METAL] ?: 0 }
    }
}