package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.DestroyDistrictUseCase

import javax.inject.Inject

class DegradePlanetUseCase @Inject constructor(
    private val destroyDistrictUseCase: DestroyDistrictUseCase
) {
    operator fun invoke(planet: Planet): Planet {
        val list: List<Int> = (0..planet.districts.size-1).toList()
        val districtToDestroy: Int = list.random()
        val updatedPlanet = destroyDistrictUseCase.invoke(planet, districtToDestroy)
        return planet.copy(
            level = planet.level - 1,
            metal = updatedPlanet.first,
            districts = updatedPlanet.second,
        )
    }
}