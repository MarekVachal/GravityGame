package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class CalculateRocketMaterialCapacityUseCase @Inject constructor() {
    operator fun invoke(planet: Planet): Int {
        return planet.districts.count {
            it.type == DistrictEnum.INDUSTRIAL && it.type == DistrictEnum.CAPITOL
        } * 10
    }
}