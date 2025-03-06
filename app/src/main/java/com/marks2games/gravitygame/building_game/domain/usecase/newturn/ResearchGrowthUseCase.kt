package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.UrbanCenterMode
import javax.inject.Inject

class ResearchGrowthUseCase @Inject constructor(){
    operator fun invoke(planet: Planet, empire: Empire): Pair <Int, Float> {
        var biomass = planet.biomass
        var research = empire.research
        val researchUrban = planet.districts
            .filterIsInstance<District.UrbanCenter>()
            .filter { it.mode == UrbanCenterMode.RESEARCH }
        val researchUrbanCount = researchUrban.count()
        if(researchUrbanCount == 0){
            return Pair(research, biomass)
        } else {
            repeat(researchUrbanCount){
                if(biomass >= 1f){
                    biomass -= 1f
                    research += 1
                } else {
                    return Pair(research, biomass)
                }
            }
        }
        return Pair(research, biomass)
    }
}