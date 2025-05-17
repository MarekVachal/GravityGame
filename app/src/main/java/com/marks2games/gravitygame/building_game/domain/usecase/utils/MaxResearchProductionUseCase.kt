package com.marks2games.gravitygame.building_game.domain.usecase.utils

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.UrbanCenterMode
import javax.inject.Inject

class MaxResearchProductionUseCase @Inject constructor() {
    operator fun invoke(planet: Planet): Int {
        return planet.districts
            .filterIsInstance<District.UrbanCenter>()
            .filter { it.mode == UrbanCenterMode.RESEARCH && it.isWorking }
            .sumOf {it.generateResources().produced[Resource.RESEARCH]?:0}
    }
}