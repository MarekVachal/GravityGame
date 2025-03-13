package com.marks2games.gravitygame.building_game.domain.usecase.resourcehelper

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class CalculateArmyProdMaxUseCase @Inject constructor() {
    operator fun invoke(planet: Planet): Int {
        val rocketMaterialsIndustrial = planet.districts
            .filterIsInstance<District.Industrial>()
            .filter { it.mode == IndustrialMode.ROCKET_MATERIALS }
        return rocketMaterialsIndustrial.count()
    }
}