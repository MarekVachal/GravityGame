package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.BuilderGameConstants.BIOMASS_CONTRIBUTION_COEFFICIENT
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculateBiomassCapacityUseCase
import javax.inject.Inject

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
class GenerateBiomassUseCase @Inject constructor(
    private val calculateBiomassCapacityUseCase: CalculateBiomassCapacityUseCase,
    private val generatePlanetOrganicSedimentsUseCase: GeneratePlanetOrganicSedimentsUseCase
) {
    operator fun invoke(planet: Planet): Float{
        val capacity = calculateBiomassCapacityUseCase.invoke(planet) * planet.biomassCapacityBonus
        val planetOSIncome = generatePlanetOrganicSedimentsUseCase.invoke(planet)

        return capacity / BIOMASS_CONTRIBUTION_COEFFICIENT - planetOSIncome
    }
}