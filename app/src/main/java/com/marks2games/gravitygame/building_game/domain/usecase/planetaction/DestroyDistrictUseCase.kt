package com.marks2games.gravitygame.building_game.domain.usecase.planetaction

import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class DestroyDistrictUseCase @Inject constructor() {
    operator fun invoke(planet: Planet, districtToDestroy: DistrictEnum): Planet {
        val districts = planet.districts.toMutableList()
        var metal = planet.metal
        val chosenDistrict = districts.find { it.type == districtToDestroy } ?: return planet

        districts.remove(chosenDistrict)
        metal += 5

        return planet.copy(
            districts = districts,
            metal = metal
        )
    }
}