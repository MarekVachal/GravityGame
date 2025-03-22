package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.BuilderGameConstants.METAL_FROM_DESTROY_DISTRICT
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

/**
 * Use case responsible for destroying a district on a planet.
 *
 * This class handles the logic for removing a district from a planet's list of districts and
 * updating the planet's metal resources as a result of the destruction.
 */
class DestroyDistrictUseCase @Inject constructor(
    private val buildDistrictUseCase: BuildDistrictUseCase
) {
    /**
     *  Simulates the action of invoking (activating) a specific district on a planet.
     *  This action removes the chosen district from the planet's district list and increases the planet's metal by 5.
     *
     * @param planet The planet on which the district is being invoked.
     * @param districtId The ID of the district to be invoked.
     * @return A new Planet object with the specified district removed and metal increased by 5,
     *         or the original planet if the specified district ID is not found.
     * @throws NoSuchElementException if planet has no districts. (implicitly thrown by `find` if list is empty).
     */
    operator fun invoke(planet: Planet, districtId: Int): Pair<Int, List<District>> {
        val districts = planet.districts.toMutableList()
        var metal = planet.metal
        val chosenDistrict = districts.find { it.districtId == districtId } ?: return Pair(planet.metal, planet.districts)

        districts.remove(chosenDistrict)
        buildDistrictUseCase.invoke(planet, DistrictEnum.EMPTY, districtId)
        metal += METAL_FROM_DESTROY_DISTRICT

        return Pair (metal, districts)
    }
}