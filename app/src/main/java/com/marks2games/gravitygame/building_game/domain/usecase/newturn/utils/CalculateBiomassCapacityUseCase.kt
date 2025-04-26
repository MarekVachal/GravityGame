package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import javax.inject.Inject

class CalculateBiomassCapacityUseCase @Inject constructor() {
    operator fun invoke(planet: Planet): Int{
        return planet.districts
            .filter { it.isWorking }
            .sumOf { it.getCapacities().capacity[Resource.BIOMASS] ?: 0 }
    }
}