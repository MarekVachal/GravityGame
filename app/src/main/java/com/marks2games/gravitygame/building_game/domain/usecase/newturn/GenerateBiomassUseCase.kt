package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject
import kotlin.math.min

/**
 * Use case responsible for calculating the biomass growth of a planet.
 *
 * This class determines the new biomass level based on the planet's current
 * biomass, the capacity for biomass growth, and a growth rate.
 *
 * Usage:
 * ```
 * val biomassGrowthUseCase = BiomassGrowthUseCase()
 * val newBiomass = biomassGrowthUseCase(myPlanet)
 * ```
 *
 * The biomass growth is calculated as follows:
 * 1. **Capacity Calculation:** The maximum biomass capacity is determined by
 *    counting the number of empty or capitol districts that are working, and then
 *    multiplying that count by 10.
 *    - `capacity = (number of empty or capitol districts) * 10`
 * 2. **Growth Calculation:**
 *    - `production = initial biomass + (capacity - initial biomass) / 10`
 * 3. **Clamping:** The final biomass is clamped to ensure it does not exceed the
 *    calculated capacity.
 *    - `final biomass = min(production, capacity)`
 *
 * This ensures that the biomass grows towards the capacity, at a rate that
 * depends on the difference between current biomass and capacity and will not exceed that capacity.
 */
class GenerateBiomassUseCase @Inject constructor() {
    /**
     * Calculates the production capacity of a given planet based on its districts and biomass.
     *
     * This function determines the maximum production capacity of a planet based on the number of
     * "working" empty districts and capitol districts, and then calculates the actual production
     * considering the current biomass level.
     *
     * The production calculation considers:
     * 1. **Capacity:** The number of "working" empty districts or capitol districts, each contributing 10 units to capacity.
     * 2. **Initial Biomass:** The starting biomass level of the planet.
     * 3. **Production Adjustment:** The difference between capacity and initial biomass, divided by 10, is added to the initial biomass.
     * 4. **Final Production:** The minimum between the adjusted production and the capacity is returned. This ensures production never exceeds the planet's capacity.
     *
     * @param planet The [Planet] object for which to calculate production.
     * @return The calculated production capacity as a Float.
     *
     * @see Planet
     * @see DistrictEnum
     */
    operator fun invoke(planet: Planet): Float{
        val capacity = planet.districts.count {
            it.type == DistrictEnum.EMPTY && it.isWorking || it.type == DistrictEnum.CAPITOL
        } * 10f

        val initial = planet.biomass
        var production = initial
        production += (capacity - initial) / 10f

        return min(production, capacity)
    }
}