package com.marks2games.gravitygame.building_game.domain.usecase.resourcehelper

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.UrbanCenterMode
import javax.inject.Inject

class CalculateResearchProdMaxUseCase @Inject constructor(){
    operator fun invoke(planet: Planet): Int {
        val researchUrban = planet.districts
            .filterIsInstance<District.UrbanCenter>()
            .filter { it.mode == UrbanCenterMode.RESEARCH }
        return researchUrban.count()
    }
}