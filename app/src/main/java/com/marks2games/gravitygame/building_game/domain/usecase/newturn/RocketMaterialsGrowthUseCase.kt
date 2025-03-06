package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class RocketMaterialsGrowthUseCase @Inject constructor() {
    operator fun invoke(planet: Planet): Triple <Int, Float, Float> {
        val rocketMaterialsCapacity = planet.districts.count {
            it.type == DistrictEnum.INDUSTRIAL
        } * 10
        var rocketMaterials = planet.rocketMaterials
        var biomass = planet.biomass
        var organicSediments = planet.organicSediment
        val rocketMaterialsIndustrial = planet.districts
            .filterIsInstance<District.Industrial>()
            .filter { it.mode == IndustrialMode.ROCKET_MATERIALS }
        val rocketMaterialsCount = rocketMaterialsIndustrial.count()
        if (rocketMaterialsCount == 0) {
            return Triple(rocketMaterials, biomass, organicSediments)
        } else {
            repeat(rocketMaterialsCount) {
                if((biomass >= 2f || organicSediments >= 2f) && rocketMaterialsCapacity > rocketMaterials){
                    rocketMaterials += 1
                    biomass -= 2f
                    organicSediments -= 2f
                } else {
                    return Triple(rocketMaterials, biomass, organicSediments)
                }
            }
        }
        return Triple(rocketMaterials, biomass, organicSediments)
    }

}