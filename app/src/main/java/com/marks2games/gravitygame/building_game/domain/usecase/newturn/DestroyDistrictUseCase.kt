package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.BuilderGameConstants.METAL_FROM_DESTROY_DISTRICT
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class DestroyDistrictUseCase @Inject constructor() {
    operator fun invoke(planet: Planet, districtId: Int): Pair<Int, List<District>> {
        val districts = planet.districts.toMutableList()
        var metal = planet.metal
        val chosenDistrict = districts.find { it.districtId == districtId } ?: return Pair(planet.metal, planet.districts)

        districts.remove(chosenDistrict)
        districts.add(District.Empty(districtId = districtId))
        if(chosenDistrict !is District.InConstruction){
            metal += METAL_FROM_DESTROY_DISTRICT
        }
        return Pair (metal, districts)
    }
}