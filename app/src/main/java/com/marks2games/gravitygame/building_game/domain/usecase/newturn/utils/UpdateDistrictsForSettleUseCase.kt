package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class UpdateDistrictsForSettleUseCase @Inject constructor() {
    operator fun invoke(districts: List<District>, planet: Planet): List<District>{
        return districts.map { district ->
            if (district.type == DistrictEnum.UNNOCUPATED && !district.isWorking ) {
                val connections =
                    planet.planetMapConfig.find { it.id.toInt() == district.districtId }?.connections
                        ?: emptyList()
                val connectedDistricts = planet.districts.filter { connectedDistrict ->
                    connections.any { it.toInt() == connectedDistrict.districtId }
                }
                if(connectedDistricts.any { it.type != DistrictEnum.UNNOCUPATED } ){
                    district.copyWithUpdatedWorking(true)
                } else {
                    district
                }
            } else {
                district
            }
        }
    }
}