package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import javax.inject.Inject

/**
 * Use case responsible for calculating the organic sediments capacity of a planet.
 *
 * This use case determines the capacity based on the number of empty or capitol districts on the planet.
 * Each empty or capitol district contributes a fixed amount to the total capacity.
 */
class CalculateOrganicSedimentsCapacityUseCase @Inject constructor() {
    /**
     * Calculates a score for a given planet based on the number of empty or capitol districts.
     *
     * This function determines the score of a planet by counting the number of districts that are either
     * of type [DistrictEnum.EMPTY] or [DistrictEnum.CAPITOL]. It then multiplies this count by 10 to arrive at
     * the final score.
     *
     * @param planet The [Planet] object for which to calculate the score.
     * @return A [Float] representing the calculated score for the planet. The score is determined by the number
     * of empty or capitol districts multiplied by 10.
     */
    operator fun invoke(planet: Planet): Float {
        return planet.districts
            .sumOf { it.getCapacities().capacity[Resource.ORGANIC_SEDIMENTS] ?: 0 }.toFloat()
    }
}
