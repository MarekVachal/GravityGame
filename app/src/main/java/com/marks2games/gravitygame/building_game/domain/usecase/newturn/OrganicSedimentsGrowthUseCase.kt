package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject
import kotlin.math.min

class OrganicSedimentsGrowthUseCase @Inject constructor(){
    operator fun invoke(planet: Planet): Float{
        val capacity = planet.districts.count {
            it.type == DistrictEnum.EMPTY || it.type == DistrictEnum.CAPITOL
        } * 10f

        val biomass = planet.biomass
        var production = planet.organicSediment
        production += biomass / 10f

        return min(production, capacity)
    }
}