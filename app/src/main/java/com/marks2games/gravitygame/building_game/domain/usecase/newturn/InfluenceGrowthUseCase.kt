package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.UrbanCenterMode
import javax.inject.Inject

class InfluenceGrowthUseCase @Inject constructor(){
    operator fun invoke(planet: Planet): Int {
        var influence = 0
        val influenceUrban = planet.districts
            .filterIsInstance<District.UrbanCenter>()
            .filter { it.mode == UrbanCenterMode.INFLUENCE }
        val influenceUrbanCount = influenceUrban.count()
        if (influenceUrbanCount == 0) {
            return 0
        } else {
            influence += (1 * influenceUrbanCount)
        }
        return influence
    }
}