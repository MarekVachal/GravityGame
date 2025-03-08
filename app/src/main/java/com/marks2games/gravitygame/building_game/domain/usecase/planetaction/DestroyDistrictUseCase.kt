package com.marks2games.gravitygame.building_game.domain.usecase.planetaction

import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.domain.repository.PlanetRepository
import javax.inject.Inject

class DestroyDistrictUseCase @Inject constructor(
    private val planetRepository: PlanetRepository
) {
    suspend operator fun invoke(planet: Planet, districtToDestroy: DistrictEnum): Planet {
        val districts = planet.districts.toMutableList()
        var metal = planet.metal
        val chosenDistrict = districts.find { it.type == districtToDestroy } ?: return planet

        districts.remove(chosenDistrict)
        metal += 5

        val updatedPlanet = planet.copy(
            districts = districts,
            metal = metal
        )

        planetRepository.updatePlanet(updatedPlanet)
        return updatedPlanet
    }
}