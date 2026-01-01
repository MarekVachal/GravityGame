package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.BuilderGameConstants.DISTRICT_BUILD_COST
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.District.*
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject
import kotlin.math.min

class FinishDistrictConstructionUseCase @Inject constructor() {
    operator fun invoke(planet: Planet, isPlanning: Boolean): Pair<List<District>, Int> {
        val districtsInConstruction = planet.districts.filterIsInstance<InConstruction>()
        var remainingInfrastructure = planet.infrastructure
        var newDistricts = planet.districts.toMutableList()

        districtsInConstruction.forEach { districtInConstruction ->
            val infrastructureNeeded = DISTRICT_BUILD_COST - districtInConstruction.infra
            val infrastructureAdded = min(remainingInfrastructure, infrastructureNeeded)

            if (infrastructureAdded > 0) {
                if(infrastructureNeeded == infrastructureAdded){
                    if(!isPlanning){
                        val districtId = districtInConstruction.districtId
                        newDistricts.removeIf { it.districtId == districtId }
                        val newDistrict = when (districtInConstruction.buildingDistrict){
                            DistrictEnum.PROSPECTORS -> Prospectors(districtId = districtId)
                            DistrictEnum.INDUSTRIAL -> Industrial(districtId = districtId)
                            DistrictEnum.EXPEDITION_PLATFORM -> ExpeditionPlatform(districtId = districtId)
                            DistrictEnum.URBAN_CENTER -> UrbanCenter(districtId = districtId)
                            else -> return@forEach
                        }
                        newDistricts.add(newDistrict)
                    }
                    remainingInfrastructure -= infrastructureAdded
                } else {
                    if(!isPlanning){
                        val index = newDistricts.indexOf(districtInConstruction)
                        newDistricts[index] = districtInConstruction.copy(
                            infra = districtInConstruction.infra + infrastructureAdded
                        )
                    }
                    remainingInfrastructure -= infrastructureAdded
                }
            }
        }
        return Pair(newDistricts.toList(), remainingInfrastructure)
    }
}