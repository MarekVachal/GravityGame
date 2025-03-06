package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.ProspectorsMode
import javax.inject.Inject
import kotlin.math.min

class MetalGrowthUseCase @Inject constructor(){
    operator fun invoke(planet: Planet): Int {
        val capacity = planet.districts.count {
            it.type == DistrictEnum.PROSPECTORS
        } * 10
        var production = planet.metal
        val metalProspectors = planet.districts
            .filterIsInstance<District.Prospectors>()
            .filter { it.mode == ProspectorsMode.METAL }
        val metalProspectorsCount = metalProspectors.count()
        if (metalProspectorsCount == 0) {
            return planet.metal
        } else {
            production += 1 * metalProspectorsCount
        }
        return min(production, capacity)
    }
}