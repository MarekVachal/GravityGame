package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import javax.inject.Inject

class CalculateRocketMaterialCapacityUseCase @Inject constructor() {
    operator fun invoke(planet: Planet): Int {
        return planet.districts
            .sumOf { it.getCapacities().capacity[Resource.BIOMASS] ?: 0 }
    }
}