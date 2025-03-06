package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class InfrastructureGrowthUseCase @Inject constructor(){
    operator fun invoke(planet: Planet): Pair <Int, Int> {
        var infrastructure = 0
        var metal = planet.metal
        val infrastructureIndustrial = planet.districts
            .filterIsInstance<District.Industrial>()
            .filter { it.mode == IndustrialMode.INFRASTRUCTURE }
        val infrastructureIndustrialCount = infrastructureIndustrial.count()
        if(infrastructureIndustrialCount == 0){
            return Pair (infrastructure, metal)
        } else {
            repeat(infrastructureIndustrialCount) {
                if (metal >= 5) {
                    infrastructure += 5
                    metal -= 5
                } else {
                    return Pair (infrastructure, metal)
                }
            }
        }
        return Pair (infrastructure, metal)
    }
}